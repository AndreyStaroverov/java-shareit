package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(@Autowired BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoCreate createBooking(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                          @RequestBody @Valid BookingDto booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoCreate updateBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                          @PathVariable(name = "bookingId") Long id,
                                          @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateBooking(userId, id, approved);
    }


    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoCreate getBookingById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                           @PathVariable @Positive Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDtoCreate> getBookingsOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDtoCreate> getBookingsItemsOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                              @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsItemsByOwner(userId, state);
    }

}
