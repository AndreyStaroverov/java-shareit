package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Collection;

public interface BookingService {
    BookingDtoCreate createBooking(Long userId, BookingDto booking);

    BookingDtoCreate updateBooking(Long userId, Long id, Boolean approved);

    BookingDtoCreate getBookingById(Long userId, Long id);

    Collection<BookingDtoCreate> getBookingsByState(Long userId, String state, Long from, Long size);

    Collection<BookingDtoCreate> getBookingsItemsByOwner(Long userId, String state, Long from, Long size);
}
