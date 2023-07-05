package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }


    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @PathVariable(name = "bookingId") Long id,
                                                @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.updateBooking(userId, id, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestParam(value = "from", required = false) @Min(0) Long from,
                                              @RequestParam(value = "size", required = false) @Min(1) Long size
    ) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingsItemsOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(value = "from", required = false) @Min(0) Long from,
                                                        @RequestParam(value = "size", required = false) @Min(1) Long size
    ) {
        return bookingClient.getBookingsOwner(userId, state, from, size);
    }


}
