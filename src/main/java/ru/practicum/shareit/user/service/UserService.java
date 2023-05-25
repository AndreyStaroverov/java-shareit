package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto userUpdate(UserDtoPatch userDto, Long id);

    UserDto getUserById(Long id);

    Collection<User> getUsers();

    void deleteUser(Long id);
}
