package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusOfBooking;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId); //ALL

    List<Booking> findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartAsc(Long bookerId, Timestamp end, Timestamp start); //CURRENT

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, Timestamp end); // PAST

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, Timestamp start); //FUTURE

    @Query(value = "select * from bookings b WHERE b.booker_id = ?1 AND b.status = ?2", nativeQuery = true)
    List<Booking> findByBooker_IdAndStatusContainingIgnoreCase(Long bookerId, String status); // WAITING & REJ

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_Id(Long ownerId); //ALL

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.status = ?2", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdStatus(Long ownerId, String status); //WAITING & REJ

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.start_date > ?2", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdFuture(Long ownerId, Timestamp start); //Future

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "and b.end_date > ?2 " +
            "and b.start_date < ?3", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdCurrent(Long ownerId, Timestamp end, Timestamp start); //Current

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.end_date < ?2", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdPast(Long ownerId, Timestamp end); //Past

    List<Booking> findByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(Long itemId, StatusOfBooking status, Timestamp start);

    List<Booking> findByItemIdAndStatusAndStartIsAfterOrderByEndAsc(Long bookerId, StatusOfBooking status, Timestamp start);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i ON b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.booker_id = ?2 " +
            "and b.status = 'APPROVED' " +
            "and start_date < ?3", nativeQuery = true)
    List<Booking> findByItemId(Long itemId, Long bookerId, Timestamp start);
}
