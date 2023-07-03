package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(ItemRequestDto itemRequest, Long userId);

    List<ItemRequestDto> getFromSize(Long userId, int from, int size);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getAllRequests(Long userId);
}
