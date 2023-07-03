package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BookingToDto {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart().toLocalDateTime(),
                booking.getEnd().toLocalDateTime(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }


    public static BookingDtoCreate toBookingDtoCreate(Booking booking) {
        return new BookingDtoCreate(
                booking.getId(),
                booking.getStart().toLocalDateTime(),
                booking.getEnd().toLocalDateTime(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Collection<BookingDtoCreate> toBookingDtoCreateCollection(Collection<Booking> bookings) {
        List<BookingDtoCreate> bookingDtoCreates = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoCreates.add(toBookingDtoCreate(booking));
        }
        bookingDtoCreates.sort(Comparator.comparing(BookingDtoCreate::getStart).reversed());
        return bookingDtoCreates;
    }

    public static Collection<BookingDtoCreate> toBookingDtoCreateCollectionCurr(Collection<Booking> bookings) {
        List<BookingDtoCreate> bookingDtoCreates = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoCreates.add(toBookingDtoCreate(booking));
        }
        return bookingDtoCreates;
    }
}
