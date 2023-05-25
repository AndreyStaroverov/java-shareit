package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.mapper.DtoToUserMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userDao.createUser(DtoToUserMapper.toUser(userDto)));
    }

    @Override
    public UserDto userUpdate(UserDtoPatch userDtoPatch, Long id) {

        User user = userDao.getUserById(id);

        if (userDtoPatch.getId() != null) {
            user.setId(userDtoPatch.getId());
        }

        if (userDtoPatch.getEmail() != null) {
            if (user.getEmail().equals(userDtoPatch.getEmail())) {
                user.setEmail(userDtoPatch.getEmail());
            } else {
                userDao.checkEmail(userDtoPatch.getEmail());
            }
            user.setEmail(userDtoPatch.getEmail());
        }

        if (userDtoPatch.getName() != null) {
            user.setName(userDtoPatch.getName());
        }
        return UserMapper.toUserDto(userDao.updateUser(user, id));
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userDao.getUserById(id));
    }

    @Override
    public Collection<User> getUsers() {
        return userDao.getAll();
    }

    @Override
    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }
}
