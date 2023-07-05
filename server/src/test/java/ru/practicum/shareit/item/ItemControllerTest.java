package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

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

    @Test
    void getItems() throws Exception {

        ArrayList<ItemDtoById> itemDtoByIds = new ArrayList<>();

        ItemDtoById itemDtoById = new ItemDtoById(1L,
                "tEST",
                "tEST", false, new BookingDto(), new BookingDto(), new ArrayList<>());
        itemDtoByIds.add(itemDtoById);

        when(itemService.getItems(any(), any(), any()))
                .thenReturn(itemDtoByIds);

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "10")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoById.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoById.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoById.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoById.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking", notNullValue()))
                .andExpect(jsonPath("$[0].comments", notNullValue()));
    }

    @Test
    void getItems_BadParams() throws Exception {

        ArrayList<ItemDtoById> itemDtoByIds = new ArrayList<>();

        ItemDtoById itemDtoById = new ItemDtoById(1L,
                "tEST",
                "tEST", false, new BookingDto(), new BookingDto(), new ArrayList<>());
        itemDtoByIds.add(itemDtoById);

        when(itemService.getItems(any(), any(), any()))
                .thenReturn(itemDtoByIds);

        Assertions.assertThrows(NestedServletException.class, () -> mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "-10")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()));
    }

    @Test
    void addItem() throws Exception {

        when(itemService.addNewItem(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void addItem_BadUserId() throws Exception {

        when(itemService.addNewItem(any(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 9999999)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem() throws Exception {

        ItemDtoPatch itemDtoPatch = new ItemDtoPatch(1L, "name", "description", true);


        when(itemService.itemUpdate(any(), any(), any()))
                .thenReturn(new ItemDto(1L, itemDtoPatch.getName(),
                        itemDtoPatch.getDescription(), itemDtoPatch.getAvailable()));

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoPatch))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoPatch.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoPatch.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoPatch.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoPatch.getAvailable())));
    }

    @Test
    void updateItem_BadId() throws Exception {

        ItemDtoPatch itemDtoPatch = new ItemDtoPatch(1L, "name", "description", true);

        when(itemService.itemUpdate(any(), any(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/items/1000000")
                        .content(mapper.writeValueAsString(itemDtoPatch))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemById() throws Exception {
        ItemDtoById itemDtoById = new ItemDtoById(1L,
                "tEST",
                "tEST", false, new BookingDto(), new BookingDto(), new ArrayList<>());

        when(itemService.getItemById(any(), any()))
                .thenReturn(itemDtoById);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoById.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoById.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoById.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoById.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", notNullValue()))
                .andExpect(jsonPath("$.nextBooking", notNullValue()))
                .andExpect(jsonPath("$.comments", notNullValue()));

    }

    @Test
    void getItemById_BadUserId() throws Exception {
        ItemDtoById itemDtoById = new ItemDtoById(1L,
                "tEST",
                "tEST", false, new BookingDto(), new BookingDto(), new ArrayList<>());

        Assertions.assertThrows(NestedServletException.class, () -> mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", -10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()));

    }

    @Test
    void getSearchItems() throws Exception {

        String text = "test";

        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(itemDto);

        when(itemService.getSearchItems(any(), any(), any()))
                .thenReturn(itemDtos);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("text", text)
                        .param("from", "10")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void getSearchItems_BadParams() throws Exception {

        String text = "test";
        Assertions.assertThrows(NestedServletException.class, () -> mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("text", text)
                        .param("from", "-10")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()));
    }

    @Test
    void addComment() throws Exception {

        CommentDto commentDto = new CommentDto(1L, "text", "Author", LocalDateTime.now());
        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void addComment_BadValidComment() throws Exception {

        CommentDto commentDto = new CommentDto(1L, "", "", LocalDateTime.now());

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}