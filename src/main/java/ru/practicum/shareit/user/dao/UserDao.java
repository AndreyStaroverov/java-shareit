package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.AlreadyExistEmailException;
import ru.practicum.shareit.exceptions.EntityNotExistException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class UserDao {

    private HashMap<Long, User> users = new HashMap();
    private Long idUser = 1L;

    public Collection<User> getAll() {
        return users.values();
    }

    public void deleteUser(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new EntityNotExistException(String.format("User with id: %d , не существует", id));
        }
    }

    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new EntityNotExistException(String.format("User with id: %d , не существует", id));
        }
    }

    public User updateUser(User user, Long id) {
        return users.put(id, user);
    }

    public User createUser(User user) {
        checkEmail(user.getEmail());
        user.setId(idUser++);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public void checkEmail(String email) {
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                throw new AlreadyExistEmailException("Такой адрес электронной почти уже существует");
            }
        }
    }

}
