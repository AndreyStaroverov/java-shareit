package ru.practicum.shareit.request.dao;

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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestRepositoryTest {

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

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(requestRepository).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    void saveRequest() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));

        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user, LocalDateTime.now()));

        Assertions.assertNotNull(itemRequest);
        Assertions.assertNotNull(itemRequest.getId());
    }

    @Test
    void findAllByRequestorNot() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user2, LocalDateTime.now()));

        ItemRequest itemRequest2 = requestRepository.save(
                new ItemRequest(2L, "text2",
                        user, LocalDateTime.now()));

        Pageable page = PageRequest.of(100 / 40, 400);

        Page<ItemRequest> itemRequestPage = requestRepository.findAllByRequestorNot(user2, page);

        Assertions.assertNotNull(itemRequestPage);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));
        User user2 = userRepository.save(new User(2L, "Test2", "test9022@mail.com"));
        ItemRequest itemRequest = requestRepository.save(
                new ItemRequest(1L, "text",
                        user2, LocalDateTime.now()));

        ItemRequest itemRequest2 = requestRepository.save(
                new ItemRequest(2L, "text2",
                        user, LocalDateTime.now()));

        Pageable page = PageRequest.of(100 / 40, 20);

        List<ItemRequest> itemRequestPage = requestRepository.findAllByRequestorIdOrderByCreatedDesc(2L);

        Assertions.assertNotNull(itemRequestPage);
        Assertions.assertEquals(1, itemRequestPage.size());
    }
}