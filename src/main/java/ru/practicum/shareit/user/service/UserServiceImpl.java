package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.mapper.DtoToUserMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            return UserMapper.toUserDto(userRepository.save(DtoToUserMapper.toUser(userDto)));
        } catch (Exception e) {
            throw new AlreadyExistEmailException("Email is used");
        }
    }

    @Override
    public UserDto userUpdate(UserDtoPatch userDtoPatch, Long id) {
        User user = userRepository.getById(id);
        if (userDtoPatch.getId() != null) {
            user.setId(userDtoPatch.getId());
        }

        if (userDtoPatch.getName() != null) {
            user.setName(userDtoPatch.getName());
        }

        if (userDtoPatch.getEmail() != null) {
            if (user.getEmail().equals(userDtoPatch.getEmail())) {
                user.setEmail(userDtoPatch.getEmail());
            } else {
                if (userRepository.getByEmailContainingIgnoreCase(userDtoPatch.getEmail()) != null) {
                    throw new AlreadyExistEmailException("Email is used");
                }
            }
            user.setEmail(userDtoPatch.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        try {
            return UserMapper.toUserDto(userRepository.getById(id));
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
    }
}
