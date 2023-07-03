package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusOfBooking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(requestRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(itemRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(bookingRepository).isNotNull();
    }

    @Test
    void saveBooking() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    void findByBooker_IdAndStatusContainingIgnoreCase() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .findByBooker_IdAndStatusContainingIgnoreCase(user2.getId(), StatusOfBooking.WAITING.toString());

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingItemsByOwner_Id() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .getBookingItemsByOwner_Id(user2.getId());

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }

    @Test
    void testGetBookingItemsByOwner_Id() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        Pageable page = PageRequest.of(100 / 400, 400);
        List<Booking> bookings = bookingRepository
                .getBookingItemsByOwner_Id(user2.getId(), page).toList();

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingItemsByOwner_IdStatus() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .getBookingItemsByOwner_IdStatus(user2.getId(), StatusOfBooking.WAITING.toString());

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingItemsByOwner_IdFuture() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .getBookingItemsByOwner_IdFuture(user2.getId(),
                        Timestamp.valueOf(LocalDateTime.now().minusHours(1)));

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingItemsByOwner_IdCurrent() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .getBookingItemsByOwner_IdFuture(user2.getId(),
                        Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)));

        assertEquals(0, bookings.size());
    }


    @Test
    void getBookingItemsByOwner_IdPast() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.WAITING
        ));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .getBookingItemsByOwner_IdPast(user2.getId(),
                        Timestamp.valueOf(LocalDateTime.now().plusHours(3)));

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }

    @Test
    void findByItemId() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Booking booking = bookingRepository.save(new Booking(
                1L, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                item, user2, StatusOfBooking.APPROVED));
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking.getId());

        List<Booking> bookings = bookingRepository
                .findByItemId(item.getId(), user2.getId(), Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)));

        assertEquals(1, bookings.size());
        assertFalse(bookings.isEmpty());
    }
}