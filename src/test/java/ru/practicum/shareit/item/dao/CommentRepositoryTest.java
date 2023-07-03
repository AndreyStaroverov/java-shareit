package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {

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
    void saveComment() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));


        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Comment comment = commentRepository.save(new Comment(1L,
                "text", item, user, LocalDateTime.now()));

        Assertions.assertNotNull(itemRequest);
        Assertions.assertNotNull(itemRequest.getId());
    }

    @Test
    void findByItemId() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));


        Item item = itemRepository.save(new Item(1L, "desc", "name", true, user2, itemRequest));

        Comment comment = commentRepository.save(new Comment(1L,
                "text", item, user, LocalDateTime.now()));

        Assertions.assertNotNull(comment);
        Assertions.assertNotNull(comment.getId());

        List<Comment> commentList = commentRepository.findByItemId(1L);

        Assertions.assertNotNull(commentList);
        Assertions.assertNotNull(commentList.get(0).getId());
        assertFalse(commentList.isEmpty());

    }
}