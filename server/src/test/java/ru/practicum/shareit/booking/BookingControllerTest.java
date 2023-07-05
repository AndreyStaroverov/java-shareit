package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto = new ItemDto(
            1L,
            "Test",
            "Description",
            true,
            1L);

    private UserDto userDto = new UserDto(
            1L,
            "john",
            "john.doe@mail.com");

    private BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1),
            new Item(1L, null, null, null, null, null),
            new User(1L, null, null),
            StatusOfBooking.WAITING);


    @Test
    void createBooking_is201() throws Exception {

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                1L,
                1L,
                StatusOfBooking.WAITING);

        when(bookingService.createBooking(any(), any())).thenReturn(bookingDtoCreate);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDtoCreate.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoCreate.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoCreate.getStatus().name())));
    }

//    @Test
//    void createBooking_isBadValidDto() throws Exception {
//
//        BookingDto bookingDto = new BookingDto(1L,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusHours(1),
//                null,
//                1L,
//                StatusOfBooking.WAITING);
//
//        when(bookingService.createBooking(any(), any())).thenReturn(bookingDtoCreate);
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingDto))
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void createBooking_isBadValid_X_sharer_User() throws Exception {
//
//        BookingDto bookingDto = new BookingDto(1L,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusHours(1),
//                1L,
//                1L,
//                StatusOfBooking.WAITING);
//
//        Assertions.assertThrows(NestedServletException.class, () -> mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(bookingDto))
//                        .header("X-Sharer-User-Id", -1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest()));
//    }

    @Test
    void updateBooking() throws Exception {

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                1L,
                1L,
                StatusOfBooking.APPROVED);
        BookingDtoCreate bookingDtoCreate2 = new BookingDtoCreate(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                new Item(1L, null, null, null, null, null),
                new User(1L, null, null),
                StatusOfBooking.APPROVED);

        when(bookingService.updateBooking(any(), any(), any())).thenReturn(bookingDtoCreate2);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDtoCreate.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoCreate.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoCreate2.getStatus().name())));
    }

    @Test
    void updateBooking_BadParams() throws Exception {

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                1L,
                1L,
                StatusOfBooking.APPROVED);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("approved", "ssls,s")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {

        when(bookingService.getBookingById(any(), any())).thenReturn(bookingDtoCreate);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDtoCreate.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoCreate.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoCreate.getStatus().name())));
    }

    @Test
    void getBookingsOwner() throws Exception {
        ArrayList<BookingDtoCreate> bookingDtoCreates = new ArrayList<>();
        bookingDtoCreates.add(bookingDtoCreate);

        when(bookingService.getBookingsByState(any(), any(), anyLong(), any())).thenReturn(bookingDtoCreates);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", String.valueOf(100))
                        .param("size", String.valueOf(100))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoCreate.getItem().getId().intValue())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoCreate.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoCreate.getStatus().name())));
    }

//    @Test
//    void getBookingsOwner_BadParams() throws Exception {
//        ArrayList<BookingDtoCreate> bookingDtoCreates = new ArrayList<>();
//        bookingDtoCreates.add(bookingDtoCreate);
//
//        when(bookingService.getBookingsByState(any(), any(), anyLong(), any())).thenReturn(bookingDtoCreates);
//
//        Assertions.assertThrows(NestedServletException.class, () -> mvc.perform(get("/bookings")
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .param("from", String.valueOf(-100))
//                        .param("size", String.valueOf(100))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest()));
//    }

    @Test
    void getBookingsItemsOwner() throws Exception {
        ArrayList<BookingDtoCreate> bookingDtoCreates = new ArrayList<>();
        bookingDtoCreates.add(bookingDtoCreate);

        when(bookingService.getBookingsItemsByOwner(any(), any(), anyLong(), any())).thenReturn(bookingDtoCreates);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", String.valueOf(100))
                        .param("size", String.valueOf(100))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoCreate.getItem().getId().intValue())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoCreate.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoCreate.getStatus().name())));
    }
}