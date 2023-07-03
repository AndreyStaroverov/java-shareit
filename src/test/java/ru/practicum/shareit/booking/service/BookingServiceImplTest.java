package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusOfBooking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InvalidDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
class BookingServiceImplTest {

    private final EntityManager em;
    private BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
    }

    @Test
    void createBooking() {
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L,
                new BookingDto(1L, LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2), 1L, 1L, StatusOfBooking.APPROVED)));
    }

    @Test
    void check_AllParams() {
        assertThrows(BadRequestException.class, () -> bookingService.check(1L, new BookingDto(1L, null,
                null, 1L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(BadRequestException.class, () -> bookingService.check(1L, new BookingDto(1L,
                LocalDateTime.now(),
                null, 1L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(NotFoundException.class, () -> bookingService.check(999L, new BookingDto(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), 1L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(NotFoundException.class, () -> bookingService.check(1L, new BookingDto(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), 999L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(BadRequestException.class, () -> bookingService.check(1L, new BookingDto(1L,
                LocalDateTime.now(),
                LocalDateTime.now().minusHours(1), 1L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(BadRequestException.class, () -> bookingService.check(1L, new BookingDto(1L,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(1), 1L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(BadRequestException.class, () -> bookingService.check(1L, new BookingDto(1L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1), 1L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(BadRequestException.class, () -> bookingService.check(1L, new BookingDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), 2L, 1L, StatusOfBooking.APPROVED)));

        assertThrows(NotFoundException.class, () -> bookingService.check(2L, new BookingDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), 1L, 1L, StatusOfBooking.APPROVED)));
    }

    @Test
    void updateBooking_BadRequests() {
        assertThrows(BadRequestException.class, () -> bookingService.updateBooking(1L, 1L, true));

        assertThrows(BadRequestException.class, () -> bookingService.updateBooking(1L, 1L, false));

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(1L, 2L, true));
    }

    @Test
    void updateBooking() {
        bookingService.updateBooking(2L, 2L, true);

        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertEquals(1L, booking.getId());
    }

    @Test
    void updateBooking_2() {
        bookingService.updateBooking(2L, 2L, false);

        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertEquals(1L, booking.getId());
    }

    @Test
    void getBookingById_Bad() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(3L, 1L));
    }

    @Test
    void getBookingById() {
        BookingDtoCreate bookingDto = bookingService.getBookingById(2L, 2L);

        assertEquals(2, bookingDto.getId());
        assertEquals(1, bookingDto.getItem().getId());
    }

    @Test
    void getBookingsByState_BadUserId() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByState(9999L, "ALL", 10L, 10L));
    }

    @Test
    void getBookingsByState_ALL() {

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "ALL", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_ALL_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "ALL", null, null);

        assertEquals(2, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_Current() {

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "CURRENT", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());

    }

    @Test
    void getBookingsByState_Current_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now().minusHours(1)),
                Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "CURRENT", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_Past() {
        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "PAST", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_Past_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now().minusHours(10)),
                Timestamp.valueOf(LocalDateTime.now().minusHours(1)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "PAST", null, null);

        assertEquals(2, bookingDtoCreates.size());
    }


    @Test
    void getBookingsByState_Future() {
        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "FUTURE", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_Future_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "FUTURE", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }


    @Test
    void getBookingsByState_Waiting() {

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                    .getBookingsByState(1L, "WAITING", 6L, 20L);
            assertEquals(1, bookingDtoCreates.size());
        });
    }

    @Test
    void getBookingsByState_Waiting_WithoutFromSize() {

        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "WAITING", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_Rejected() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                    .getBookingsByState(2L, "REJECTED", 100L, 100L);

            assertEquals(0, bookingDtoCreates.size());
        });

    }

    @Test
    void getBookingsByState_Rejected_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.REJECTED));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsByState(1L, "REJECTED", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }

    @Test
    void getBookingsByState_Default() {
        assertThrows(InvalidDataException.class, () -> bookingService
                .getBookingsByState(1L, "Sllaa", 10L, 10L));
    }

    @Test
    void getBookingsItemsByOwner_BadUserId() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsItemsByOwner(9999L, "ALL", 10L, 10L));
    }

    @Test
    void getBookingsItemsByOwner_ALL() {
        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(1L, "ALL", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_ALL_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(2L, "ALL", null, null);

        assertEquals(2, bookingDtoCreates.size());

    }

    @Test
    void getBookingsItemsByOwner_Current() {
        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(1L, "CURRENT", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_Current_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now().minusHours(1)),
                Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(2L, "CURRENT", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_Past() {
        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(1L, "PAST", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_Past_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now().minusHours(10)),
                Timestamp.valueOf(LocalDateTime.now().minusHours(1)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(2L, "PAST", null, null);

        assertEquals(2, bookingDtoCreates.size());
    }


    @Test
    void getBookingsItemsByOwner_Future() {
        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(1L, "FUTURE", 100L, 100L);

        assertEquals(0, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_Future_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(2L, "FUTURE", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }


    @Test
    void getBookingsItemsByOwner_Waiting() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                    .getBookingsItemsByOwner(1L, StatusOfBooking.WAITING.toString(), 100L, 100L);
            assertEquals(0, bookingDtoCreates.size());
        });
    }

    @Test
    void getBookingsItemsByOwner_Waiting_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.WAITING));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(2L, "WAITING", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_Rejected() {

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                    .getBookingsItemsByOwner(1L, "REJECTED", 100L, 100L);
            assertEquals(0, bookingDtoCreates.size());
        });

    }

    @Test
    void getBookingsItemsByOwner_Rejected_WithoutFromSize() {
        bookingRepository.save(new Booking(2L,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now().plusHours(2)),
                itemRepository.getById(1L),
                userRepository.getById(1L),
                StatusOfBooking.REJECTED));

        Collection<BookingDtoCreate> bookingDtoCreates = bookingService
                .getBookingsItemsByOwner(2L, "REJECTED", null, null);

        assertEquals(1, bookingDtoCreates.size());
    }

    @Test
    void getBookingsItemsByOwner_Default() {
        assertThrows(InvalidDataException.class, () -> bookingService
                .getBookingsItemsByOwner(1L, "Sllaa", 10L, 10L));
    }


    @Test
    void checkUserIdAndBookingId() {
        assertThrows(NotFoundException.class, () -> bookingService.checkUserIdAndBookingId(1L, 999L));

        assertThrows(NotFoundException.class, () -> bookingService.checkUserIdAndBookingId(99L, 1L));
    }
}