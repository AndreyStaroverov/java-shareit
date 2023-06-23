package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.Collection;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDto toItemDtoRequest(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static Collection<ItemRequestDto> toDtoCollection(Collection<ItemRequest> itemRequests) {
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(toItemDtoRequest(itemRequest));
        }
        return itemRequestDtos;
    }

}
