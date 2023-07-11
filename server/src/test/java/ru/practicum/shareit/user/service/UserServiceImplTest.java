package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = "/test-data.sql")
class UserServiceImplTest {

    private final EntityManager em;
    private UserService service;
    private final UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        service = new UserServiceImpl(userRepository);
    }

    @Test
    void saveUser() {

        UserDto userDto = new UserDto(1L, "Jame", "jame@mail.ru");
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertEquals(user.getName(), user.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void saveUser_emailAlreadyUsed() {

        UserDto userDto1 = new UserDto(null, "Jame", "jame@mail.ru");
        assertThrows(AlreadyExistEmailException.class, () -> service.createUser(userDto1));

    }

    @Test
    void userUpdate() {

        service.userUpdate(new UserDtoPatch(1L, "JameUpdated", "jame123@mail.ru"), 1L);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", "jame123@mail.ru")
                .getSingleResult();

        assertEquals("JameUpdated", user.getName());
        assertEquals("jame123@mail.ru", user.getEmail());

    }

    @Test
    void userUpdate_EmailAlreadyUsed() {

        assertThrows(AlreadyExistEmailException.class, () ->
                service.userUpdate(new UserDtoPatch(null, "Jom", "jameAwer@mail.ru"), 1L));

    }

    @Test
    void userUpdate_EmailAlreadyUsed_isOk() {

        service.userUpdate(new UserDtoPatch(null, "Jom", "jameAwer@mail.ru"), 2L);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", "jameAwer@mail.ru")
                .getSingleResult();

        assertEquals(2, user.getId());
        assertEquals("Jom", user.getName());
        assertEquals("jameAwer@mail.ru", user.getEmail());
    }

    @Test
    void userUpdate_EmailAlreadyUsed_isOk1() {
        service.userUpdate(new UserDtoPatch(1L, null, null), 1L);
    }

    @Test
    void getUserById() {

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", "jameAwer@mail.ru")
                .getSingleResult();

        assertEquals(service.getUserById(2L).getId(), user.getId());
        assertEquals(service.getUserById(2L).getName(), user.getName());
        assertEquals(service.getUserById(2L).getEmail(), user.getEmail());
    }

    @Test
    void getUserById_NotFound() {
        assertThrows(NotFoundException.class, () -> service.getUserById(9999L));
    }

    @Test
    void getUsers() {
        assertEquals(service.getUsers().size(), 3);
    }

    @Test
    void deleteUser() {
        assertEquals(service.getUsers().size(), 3);
        service.deleteUser(2L);
    }

    @Test
    void deleteUser_BadId() {
        assertThrows(NotFoundException.class, () -> service.deleteUser(999999L));
    }
}