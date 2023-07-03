package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusOfBooking;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId); //ALL

    Page<Booking> findByBooker_Id(Long bookerId, Pageable page); //ALL pageable

    List<Booking> findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartAsc(Long bookerId, Timestamp end, Timestamp start); //CURRENT

    Page<Booking> findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByStartAsc(Long bookerId, Timestamp end, Timestamp start, Pageable page);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, Timestamp end); // PAST

    Page<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, Timestamp end, Pageable page);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, Timestamp start); //FUTURE

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, Timestamp start, Pageable page);

    @Query(value = "select * from bookings b WHERE b.booker_id = ?1 AND b.status = ?2", nativeQuery = true)
    List<Booking> findByBooker_IdAndStatusContainingIgnoreCase(Long bookerId, String status); // WAITING & REJ

    Page<Booking> findByBookerAndStatusContaining(Long bookerId, String status, Pageable page);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_Id(Long ownerId); //ALL owner

    @Query(value = "select b from Booking b Where b.item.owner.id = ?1")
    Page<Booking> getBookingItemsByOwner_Id(Long ownerId, Pageable page); //ALL owner PAGE

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.status = ?2", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdStatus(Long ownerId, String status); //WAITING & REJ

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    Page<Booking> getBookingItemsByOwner_IdStatus(Long ownerId, String status, Pageable page); //WAITING & REJ PAGE Ow

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.start_date > ?2", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdFuture(Long ownerId, Timestamp start); //Future

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    Page<Booking> getBookingItemsByOwner_IdFuture(Long ownerId, Timestamp start, Pageable page); //Future page

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 " +
            "and b.end_date > ?2 " +
            "and b.start_date < ?3", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdCurrent(Long ownerId, Timestamp end, Timestamp start); //Current

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.end > ?2 and b.start < ?3")
    Page<Booking> getBookingItemsByOwner_IdCurrent(Long ownerId, Timestamp end, Timestamp start, Pageable page); //Current page

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.owner_id = ?1 and b.end_date < ?2", nativeQuery = true)
    List<Booking> getBookingItemsByOwner_IdPast(Long ownerId, Timestamp end); //Past

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    Page<Booking> getBookingItemsByOwner_IdPast(Long ownerId, Timestamp end, Pageable page); //Past page

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
