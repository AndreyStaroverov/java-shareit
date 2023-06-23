package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemRequestDto add(ItemRequestDto itemRequestDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found in base");
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userRepository.getById(userId));
        return ItemRequestMapper.toItemDtoRequest(requestRepository.save(itemRequest));

    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ItemRequestDto> getFromSize(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found in base");
        }
        Sort sortById = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from / size, size, sortById);

        Page<ItemRequest> requestsPage = requestRepository.findAllByRequestorNot(userRepository.getById(userId), page);
        requestsPage.getContent().forEach(itemRequest -> {
        });
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>(ItemRequestMapper.toDtoCollection(requestsPage.get()
                .collect(Collectors.toList())).stream().collect(Collectors.toList()));
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            itemRequestDto.setItems(ItemMapper.toItemDtoCollectionRequests(itemRepository.findAllNotId(userId)));
        }
        return itemRequestDtos;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found in base");
        }
        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException("Request Not Found");
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemDtoRequest(requestRepository.getById(requestId));
        itemRequestDto.setItems(ItemMapper.toItemDtoCollectionRequests(itemRepository.findAllByRequestId(requestId)));
        return itemRequestDto;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found in base");
        }
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>(ItemRequestMapper.toDtoCollection(requestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId)));
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            itemRequestDto.setItems(ItemMapper.toItemDtoCollectionRequests(itemRepository.findAllByRequestorId(userId)));
        }
        return itemRequestDtos;
    }
}
