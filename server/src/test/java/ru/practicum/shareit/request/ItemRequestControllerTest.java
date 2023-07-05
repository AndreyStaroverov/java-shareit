package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "tEST", 2L,
            LocalDateTime.now(), new ArrayList<>());

    private UserDto userDto = new UserDto(
            1L,
            "john",
            "john.doe@mail.com");


    @Test
    void createRequest() throws Exception {

        when(itemRequestService.add(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor().intValue())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems())));
    }

//    @Test
//    void createRequest_EmptyDesc_400() throws Exception {
//
//        mvc.perform(post("/requests")
//                        .content(mapper.writeValueAsString(new ItemRequestDto(1L, "",
//                                null, null, null)))
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void createRequestWithoutUser_404() throws Exception {
        when(itemRequestService.add(any(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequest() throws Exception {

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void getRequest_0k() throws Exception {
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        itemRequestDtos.add(itemRequestDto);

        when(itemRequestService.getAllRequests(any())).thenReturn(itemRequestDtos);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andReturn();

    }

//    @Test
//    void getRequest_NegativeId() throws Exception {
//        try {
//            mvc.perform(get("/requests")
//                            .header("X-Sharer-User-Id", -2L)
//                            .characterEncoding(StandardCharsets.UTF_8)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .accept(MediaType.APPLICATION_JSON))
//                    .andExpect(status().is5xxServerError());
//        } catch (NestedServletException e) {
//            System.out.println("Bad Id");
//        }
//    }

    @Test
    void getRequests_All() throws Exception {
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        itemRequestDtos.add(itemRequestDto);

        when(itemRequestService.getFromSize(any(), anyInt(), anyInt())).thenReturn(itemRequestDtos);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "10")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andReturn();
    }

//    @Test
//    void getRequests_All_BadParams() throws Exception {
//        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>();
//        itemRequestDtos.add(itemRequestDto);
//
//        when(itemRequestService.getFromSize(any(), anyInt(), anyInt())).thenReturn(itemRequestDtos);
//
//        assertThrows(NestedServletException.class, () -> mvc.perform(get("/requests/all")
//                        .content(mapper.writeValueAsString(itemRequestDto))
//                        .header("X-Sharer-User-Id", userDto.getId())
//                        .param("from", "-10")
//                        .param("size", "10")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NestedServletException))
//                .andExpect(status().isBadRequest()));
//    }

    @Test
    void getRequestById() throws Exception {

        when(itemRequestService.getRequestById(any(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor().intValue())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems())));
    }

    @Test
    void getRequestById_BadId() throws Exception {

        when(itemRequestService.getRequestById(any(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}