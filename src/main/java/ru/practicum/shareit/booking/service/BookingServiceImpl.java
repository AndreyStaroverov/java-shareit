package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.SortBookings;
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

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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

    private void check(Long userId, BookingDto booking) {
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
    public Collection<BookingDtoCreate> getBookingsByState(Long userId, String state) {
        if (userRepository.existsById(userId)) {
            switch (state) {
                case "ALL":
                    List<Booking> bookingsAll = new ArrayList<>(bookingRepository.findByBooker_Id(userId));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsAll);
                case "CURRENT":
                    List<Booking> bookingsCur = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartAsc(
                                    userId, Timestamp.valueOf(LocalDateTime.now()),
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollectionCurr(bookingsCur);
                case "PAST":
                    List<Booking> bookingsPast = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsPast);
                case "FUTURE":
                    List<Booking> bookingsFuture = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndStartIsAfter(userId,
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsFuture);
                case "WAITING":
                    List<Booking> bookingsWait = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndStatusContainingIgnoreCase(
                                    userId, SortBookings.WAITING.toString()));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsWait);
                case "REJECTED":
                    List<Booking> bookingsRej = new ArrayList<>(
                            bookingRepository.findByBooker_IdAndStatusContainingIgnoreCase(
                                    userId, SortBookings.REJECTED.toString()));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsRej);
                default:
                    throw new InvalidDataException("Unknown state: " + state);
            }
        } else {
            throw new NotFoundException("Not Found User");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<BookingDtoCreate> getBookingsItemsByOwner(Long userId, String state) {
        if (userRepository.existsById(userId)) {
            switch (state) {
                case "ALL":
                    List<Booking> bookingsAll = new ArrayList<>(bookingRepository.getBookingItemsByOwner_Id(userId));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsAll);
                case "CURRENT":
                    List<Booking> bookingsCur = new ArrayList<>(bookingRepository
                            .getBookingItemsByOwner_IdCurrent(userId,
                                    Timestamp.valueOf(LocalDateTime.now()),
                                    Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsCur);
                case "PAST":
                    List<Booking> bookingsPast = new ArrayList<>(bookingRepository
                            .getBookingItemsByOwner_IdPast(userId, Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsPast);
                case "FUTURE":
                    System.out.println(Timestamp.valueOf(LocalDateTime.now()));
                    List<Booking> bookingsFuture = new ArrayList<>(bookingRepository
                            .getBookingItemsByOwner_IdFuture(userId, Timestamp.valueOf(LocalDateTime.now())));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsFuture);
                case "WAITING":
                    List<Booking> bookingsWait = new ArrayList<>(
                            bookingRepository.getBookingItemsByOwner_IdStatus(userId, SortBookings.WAITING.toString())
                    );
                    return BookingToDto.toBookingDtoCreateCollection(bookingsWait);
                case "REJECTED":
                    List<Booking> bookingsRej = new ArrayList<>(
                            bookingRepository.getBookingItemsByOwner_IdStatus(userId, SortBookings.REJECTED.toString()));
                    return BookingToDto.toBookingDtoCreateCollection(bookingsRej);
                default:
                    throw new InvalidDataException("Unknown state: " + state);
            }
        } else {
            throw new NotFoundException("UserNotFound");
        }
    }

    private void checkUserIdAndBookingId(Long userId, Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Not Found Booking");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
    }
}
