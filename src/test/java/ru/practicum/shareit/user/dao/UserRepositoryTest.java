package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    UserRepository userRepository;


    @Test
    void getByEmail_Test() {
        User user = new User(1L, "Test", "testemail@mail.com");
        userRepository.save(user);

        User userByEmail = userRepository.getByEmailContainingIgnoreCase("testemail@mail.com");

        assertEquals(user.getEmail(), userByEmail.getEmail());
        assertEquals(user.getName(), userByEmail.getName());
        Assertions.assertNotNull(userByEmail.getId());

    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    void userTest_save() {
        User user = userRepository.save(new User(1L, "Test", "test90@mail.com"));

        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void userTest_findById() {
        User userSave = userRepository.save(new User(1L, "Test", "test901@mail.com"));

        Optional<User> user = userRepository.findById(1L);
        Assertions.assertNotNull(user);
    }
}