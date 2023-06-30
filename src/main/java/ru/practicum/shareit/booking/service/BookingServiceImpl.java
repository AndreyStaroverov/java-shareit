package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusOfBooking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingToDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InvalidDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final String SORT_BY_DATE = "start";

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDtoCreate createBooking(Long userId, BookingDto booking) {
        check(userId, booking);
        Booking books = new Booking();
        try {
            books.setStatus(StatusOfBooking.WAITING);
            books.setBooker(userRepository.getById(userId));
            books.setItem(itemRepository.getById(booking.getItemId()));
            books.setEnd(Timestamp.valueOf(booking.getEnd()));
            books.setStart(Timestamp.valueOf(booking.getStart()));

            return BookingToDto.toBookingDtoCreate(bookingRepository.save(books));
        } catch (Exception e) {
            throw new NotFoundException("No user or item");
        }
    }

    public void check(Long userId, BookingDto booking) {
        if (booking.getStart() == null || booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Start is null or equal End");
        }
        if (booking.getEnd() == null) {
            throw new BadRequestException("End is null");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        if (!itemRepository.existsById(booking.getItemId())) {
            throw new NotFoundException("Item Not Found");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Time End in Past");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BadRequestException("Time end before start");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start in Past");
        }
        if (itemRepository.getById(booking.getItemId()).getAvailable().equals(false)) {
            throw new BadRequestException("Now item is NoAvailable");
        }
        if (itemRepository.getById(booking.getItemId()).getOwner().getId().equals(userId)) {
            throw new NotFoundException("You cant Booking your Item");
        }

    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDtoCreate updateBooking(Long userId, Long id, Boolean approved) {
        checkUserIdAndBookingId(userId, id);
        Booking booking = bookingRepository.getById(id);

        if (booking.getStatus().equals(StatusOfBooking.APPROVED)) {
            throw new BadRequestException("Booking already APPROVED");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("No rules for booking");
        }
        if (approved) {
            booking.setStatus(StatusOfBooking.APPROVED);
        } else {
            booking.setStatus(StatusOfBooking.REJECTED);
        }
        return BookingToDto.toBookingDtoCreate(bookingRepository.save(booking));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public BookingDtoCreate getBookingById(Long userId, Long id) {
        checkUserIdAndBookingId(userId, id);
        Booking booking = bookingRepository.getById(id);
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingToDto.toBookingDtoCreate(bookingRepository.getById(id));
        } else {
            throw new NotFoundException("No rules for booking");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<BookingDtoCreate> getBookingsByState(Long userId, String state, Long from, Long size) {
        if (userRepository.existsById(userId)) {
            switch (state) {
                case "ALL":
                    if (from != null && size != null) {
                        return pageAllBookings(userId, from, size);
                    }
                    List<Booking> bookingsAll = new ArrayList<>(bookingRepository.findByBooker_Id(userId));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsAll);
                case "CURRENT":
                    if (from != null && size != null) {
                        return pageCurrentBookings(userId, from, size);
                    }
                    List<Booking> bookingsCur = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartAsc(
                                    userId, Timestamp.valueOf(LocalDateTime.now()),
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollectionCurr(bookingsCur);
                case "PAST":
                    if (from != null && size != null) {
                        return pagePastBookings(userId, from, size);
                    }
                    List<Booking> bookingsPast = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsPast);
                case "FUTURE":
                    if (from != null && size != null) {
                        return pageFutureBookings(userId, from, size);
                    }
                    List<Booking> bookingsFuture = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndStartIsAfter(userId,
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsFuture);
                case "WAITING":
                    if (from != null && size != null) {
                        return pageWaitingBookings(userId, from, size);
                    }
                    List<Booking> bookingsWait = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndStatusContainingIgnoreCase(
                                    userId, StatusOfBooking.WAITING.toString()));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsWait);
                case "REJECTED":
                    if (from != null && size != null) {
                        return pageRejectedBookings(userId, from, size);
                    }
                    List<Booking> bookingsRej = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndStatusContainingIgnoreCase(
                                    userId, StatusOfBooking.REJECTED.toString()));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsRej);
                default:
                    throw new InvalidDataException("Unknown state: " + state);
            }
        } else {
            throw new NotFoundException("Not Found User");
        }
    }

    public Collection<BookingDtoCreate> pageCurrentBookings(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartAsc(
                userId, Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollectionCurr(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageAllBookings(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository.findByBooker_Id(userId, page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pagePastBookings(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                Timestamp.valueOf(LocalDateTime.now()), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageFutureBookings(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository.findByBooker_IdAndStartIsAfter(userId,
                Timestamp.valueOf(LocalDateTime.now()), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageWaitingBookings(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository.findByBookerAndStatusContaining(
                userId, StatusOfBooking.WAITING.toString(), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageRejectedBookings(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository.findByBookerAndStatusContaining(
                userId, StatusOfBooking.REJECTED.toString(), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<BookingDtoCreate> getBookingsItemsByOwner(Long userId, String state, Long from, Long size) {
        if (userRepository.existsById(userId)) {
            switch (state) {
                case "ALL":
                    if (from != null && size != null) {
                        return pageAllBookingsOwner(userId, from, size);
                    }
                    List<Booking> bookingsAll = new ArrayList<>(bookingRepository.getBookingItemsByOwner_Id(userId));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsAll);
                case "CURRENT":
                    if (from != null && size != null) {
                        return pageCurrentBookingsOwner(userId, from, size);
                    }
                    List<Booking> bookingsCur = new ArrayList<>(bookingRepository
                            .getBookingItemsByOwner_IdCurrent(userId,
                                    Timestamp.valueOf(LocalDateTime.now()),
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsCur);
                case "PAST":
                    if (from != null && size != null) {
                        return pagePastBookingsOwner(userId, from, size);
                    }
                    List<Booking> bookingsPast = new ArrayList<>(bookingRepository
                            .getBookingItemsByOwner_IdPast(userId, Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsPast);
                case "FUTURE":
                    if (from != null && size != null) {
                        return pageFutureBookingsOwner(userId, from, size);
                    }
                    List<Booking> bookingsFuture = new ArrayList<>(bookingRepository
                            .getBookingItemsByOwner_IdFuture(userId, Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsFuture);
                case "WAITING":
                    if (from != null && size != null) {
                        return pageWaitingBookingsOwner(userId, from, size);
                    }
                    List<Booking> bookingsWait = new ArrayList<>(
                            bookingRepository.getBookingItemsByOwner_IdStatus(userId, StatusOfBooking.WAITING.toString())
                    );
                    return BookingToDto.toBookingDtoCreateCollection(bookingsWait);
                case "REJECTED":
                    if (from != null && size != null) {
                        return pageRejectedBookingsOwner(userId, from, size);
                    }
                    List<Booking> bookingsRej = new ArrayList<>(
                            bookingRepository.getBookingItemsByOwner_IdStatus(userId, StatusOfBooking.REJECTED.toString()));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsRej);
                default:
                    throw new InvalidDataException("Unknown state: " + state);
            }
        } else {
            throw new NotFoundException("UserNotFound");
        }
    }

    public Collection<BookingDtoCreate> pageCurrentBookingsOwner(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository
                .getBookingItemsByOwner_IdCurrent(userId,
                        Timestamp.valueOf(LocalDateTime.now()),
                        Timestamp.valueOf(LocalDateTime.now()), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageAllBookingsOwner(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);
        Page<Booking> bookingsPage = bookingRepository.getBookingItemsByOwner_Id(userId, page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pagePastBookingsOwner(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository
                .getBookingItemsByOwner_IdPast(userId, Timestamp.valueOf(LocalDateTime.now()), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageFutureBookingsOwner(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository
                .getBookingItemsByOwner_IdFuture(userId, Timestamp.valueOf(LocalDateTime.now()), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageWaitingBookingsOwner(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository
                .getBookingItemsByOwner_IdStatus(userId, StatusOfBooking.WAITING.toString(), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public Collection<BookingDtoCreate> pageRejectedBookingsOwner(Long userId, Long from, Long size) {
        Sort sortByDate = Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size), sortByDate);

        Page<Booking> bookingsPage = bookingRepository
                .getBookingItemsByOwner_IdStatus(userId, StatusOfBooking.REJECTED.toString(), page);
        return new ArrayList<>(BookingToDto.toBookingDtoCreateCollection(bookingsPage.get()
                .collect(Collectors.toList())));
    }

    public void checkUserIdAndBookingId(Long userId, Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Not Found Booking");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
    }
}
