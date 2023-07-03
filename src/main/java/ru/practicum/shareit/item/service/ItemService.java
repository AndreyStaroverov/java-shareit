package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDtoById> getItems(Long userId, Long from, Long size);

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    ItemDto itemUpdate(ItemDtoPatch itemDtoPatch, Long id, Long userId);

    ItemDtoById getItemById(Long id, Long userId);

    Collection<ItemDto> getSearchItems(String text, Long from, Long size);

    CommentDto addComment(Long userId, Comment comment, Long itemId);
}
