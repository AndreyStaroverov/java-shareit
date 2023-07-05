package ru.practicum.shareit.item.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class MapperTest {

    @Test
    void toCommentDto() {
        CommentDto commentDto = CommentMapper.toCommentDto(new Comment(1L,
                "text",
                new Item(1L, "desc", "name", true, new User(), new ItemRequest()),
                new User(1L, "NAME", "email@mail.ru"),
                LocalDateTime.now()));

        assertThat(commentDto).isNotNull();
        assertEquals(1, commentDto.getId());
        assertEquals("text", commentDto.getText());
        assertEquals("NAME", commentDto.getAuthorName());
    }

    @Test
    void toCommentDtoCollection() {
        Comment comment = new Comment(1L,
                "text",
                new Item(1L, "desc", "name", true, new User(), new ItemRequest()),
                new User(1L, "NAME", "email@mail.ru"),
                LocalDateTime.now());
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Collection<CommentDto> commentDtos = CommentMapper.toCommentDtoCollection(comments);

        assertThat(commentDtos.size()).isNotNull();
        assertFalse(commentDtos.isEmpty());
    }

    @Test
    void toItemDtoRequest() {
        Item item = new Item(1L, "desc", "name", true,
                new User(1L, "NAME", "email@mail.ru"),
                new ItemRequest(1L, "text", new User(), LocalDateTime.now()));
        List<Item> items = new ArrayList<>();
        items.add(item);

        Collection<ItemDtoById> itemDtos = ItemMapper.toItemDtoCollectionItems(items);

        assertThat(itemDtos.size()).isNotNull();
        assertFalse(itemDtos.isEmpty());

    }


}