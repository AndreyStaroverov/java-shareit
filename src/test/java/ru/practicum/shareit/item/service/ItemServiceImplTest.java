package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
class ItemServiceImplTest {

    private final EntityManager em;
    private ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository);
    }

    @Test
    void getItems() {

        Collection<ItemDtoById> itemDtos = itemService.getItems(1L, 100L, 100L);
        assertEquals(0, itemDtos.size());

    }

    @Test
    void getItems_WithoutParams() {

        Collection<ItemDtoById> itemDtos = itemService.getItems(1L, null, null);
        assertEquals(0, itemDtos.size());

    }

    @Test
    void addNewItem_Bad() {
        ItemDto itemDto = new ItemDto(null, "name", "test", true);
        assertThrows(DataIntegrityViolationException.class, () -> itemService.addNewItem(1L, itemDto));
    }

    @Test
    void addNewItem_BadUserId() {
        ItemDto itemDto = new ItemDto(1L, "name", "test", true);
        assertThrows(NotFoundException.class, () -> itemService.addNewItem(1000L, itemDto));
    }

    @Test
    void addNewItem_RequestId() {
        ItemDto itemDto = new ItemDto(1L, "name", "test", true, 1L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
    }

    @Test
    void deleteItem_BadOwner() {
        assertThrows(NotOwnerException.class, () -> itemService.deleteItem(1L, 1L));
    }

    @Test
    void deleteItem_BadItem() {
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(1L, 999L));
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(2L, 1L);
    }

    @Test
    void itemUpdate() {
        itemService.itemUpdate(new ItemDtoPatch(1L, "update", "update", true), 1L, 2L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertEquals(1, item.getId());
        assertEquals("update", item.getName());
    }

    @Test
    void itemUpdate_BadOwner() {
        assertThrows(NotOwnerException.class, () -> itemService.itemUpdate(new ItemDtoPatch(1L, "update",
                "update", true), 1L, 1L));
    }

    @Test
    void getItemById() {
        ItemDtoById itemTest = itemService.getItemById(1L, 2L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertEquals(itemTest.getId(), item.getId());
    }

    @Test
    void getItemById_BadId() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(9999L, 2L));
    }

    @Test
    void getSearchItems_textBlank() {
        assertEquals(0, itemService.getSearchItems("", 100L, 100L).size());
    }

    @Test
    void getSearchItems() {

        Collection<ItemDto> itemDtos = itemService.getSearchItems("test", 100L, 100L);
        assertEquals(0, itemDtos.size());

        Collection<ItemDto> itemDtos2 = itemService.getSearchItems("test", null, null);
        assertEquals(0, itemDtos.size());
    }

    @Test
    void addComment_BadUser() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(999L, new Comment(), 1L));
    }

    @Test
    void addComment_BadItem() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, new Comment(), 9999L));
    }

    @Test
    void addComment_BadSharer() {
        assertThrows(BadRequestException.class, () -> itemService.addComment(2L, new Comment(), 1L));
    }

    @Test
    void addComment() {
        itemService.addComment(1L, new Comment(null, "comment", null, null, null), 1L);

        TypedQuery<Comment> query = em.createQuery("Select i from Comment i where i.id = :id", Comment.class);
        Comment comment = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertEquals(1, comment.getId());
        assertEquals("comment", comment.getText());
    }
}
