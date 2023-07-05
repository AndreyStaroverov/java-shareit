package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = "/test-data.sql")
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(requestRepository, userRepository, itemRepository);
    }

    @Test
    void add() {

        ItemRequestDto itemRequestDto = new ItemRequestDto(2L, "tEST", 2L,
                LocalDateTime.now(), new ArrayList<>());

        assertThrows(DataIntegrityViolationException.class, () -> itemRequestService.add(itemRequestDto, 2L));
    }

    @Test
    void add_BadUserId() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "tEST", 2L,
                LocalDateTime.now(), new ArrayList<>());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.add(itemRequestDto, 99999L));
    }

    @Test
    void getFromSize() {

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getFromSize(1L, 10, 10);
        assertEquals(0, itemRequestDtos.size());

        itemRequestService.add(new ItemRequestDto(1L,
                "Test",
                2L,
                LocalDateTime.now(),
                null), 1L);

        assertEquals(itemRequestService.getFromSize(2L, 10, 10).size(), itemRequestDtos.size());


    }

    @Test
    void getFromSize_BadUser() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getFromSize(99999L, 100, 100));
    }

    @Test
    void getRequestById() {
        requestRepository.save(new ItemRequest(1L, "test", userRepository.getById(1L), LocalDateTime.now()));

        assertEquals(itemRequestService.getRequestById(1L, 1L).getDescription(), "test");
    }

    @Test
    void getRequestById_BadId() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(99999L, 1L));
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 999990L));
    }

    @Test
    void getAllRequests_BadId() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(99999L));
    }

    @Test
    void getAllRequests() {

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllRequests(1L);
        assertEquals(1, itemRequestDtos.size());

        assertEquals(itemRequestService.getAllRequests(1L).size(), itemRequestDtos.size());

    }
}