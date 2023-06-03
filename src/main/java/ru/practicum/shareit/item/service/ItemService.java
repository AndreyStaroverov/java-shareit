package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    ItemDto itemUpdate(ItemDtoPatch itemDtoPatch, Long id, Long userId);

    ItemDto getItemById(Long id);

    Collection<ItemDto> getSearchItems(String text);
}
