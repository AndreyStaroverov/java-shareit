package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(requestRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(itemRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
    }

    @Test
    void saveItem() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Assertions.assertNotNull(item);
        Assertions.assertNotNull(item.getId());
    }

    @Test
    void getSearchItems() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Assertions.assertNotNull(item);
        Assertions.assertNotNull(item.getId());

        List<Item> items = itemRepository.getSearchItems("desc");

        assertEquals(1, items.size());
        assertEquals("desc", items.get(0).getName());
    }

    @Test
    void testGetSearchItems() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Assertions.assertNotNull(item);
        Assertions.assertNotNull(item.getId());

        Pageable page = PageRequest.of(100 / 400, 400);
        Page<Item> items = itemRepository.getSearchItems("desc", page);

        assertEquals(1, items.get().collect(Collectors.toList()).size());
        assertEquals("desc", items.get().collect(Collectors.toList()).get(0).getName());
    }

    @Test
    void findAllByRequestorId() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user, itemRequest));

        Assertions.assertNotNull(item);
        Assertions.assertNotNull(item.getId());

        List<Item> items = itemRepository.findAllByRequestorId(1L);

        assertEquals(0, items.size());
    }

    @Test
    void findAllByRequestId() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user, itemRequest));

        Assertions.assertNotNull(item);
        Assertions.assertNotNull(item.getId());

        List<Item> items = itemRepository.findAllByRequestId(1L);

        assertEquals(0, items.size());
    }

    @Test
    void findAllNotId() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Assertions.assertNotNull(item);
        Assertions.assertNotNull(item.getId());

        List<Item> items = itemRepository.findAllNotId(1L);

        assertEquals(1, items.size());

    }
}