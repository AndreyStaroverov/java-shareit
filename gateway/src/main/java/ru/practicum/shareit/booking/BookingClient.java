package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> bookItem(Long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long id, Boolean approved) {
        return patch("/" + id + "?approved=" + approved, userId.longValue());
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(Long userId, String state, Long from, Long size) {
        if (from != null && size != null && state != null) {
            Map<String, Object> parameters = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            return get("?state={state}&from={from}&size={size}", userId, parameters);
        }
        if (state != null) {
            return get("?state=" + state, userId);
        } else {
            return get("", userId);
        }
    }

    public ResponseEntity<Object> getBookingsOwner(Long userId, String state, Long from, Long size) {
        if (from != null && size != null && state != null) {
            Map<String, Object> parameters = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
        }
        if (state != null) {
            return get("/owner?state=" + state, userId);
        } else {
            return get("/owner", userId);
        }
    }

}
