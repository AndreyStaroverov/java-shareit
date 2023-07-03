package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {


    Page<ItemRequest> findAllByRequestorNot(User requestor, Pageable page);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);
}
