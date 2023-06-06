package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByEmailContainingIgnoreCase(String email);

}
