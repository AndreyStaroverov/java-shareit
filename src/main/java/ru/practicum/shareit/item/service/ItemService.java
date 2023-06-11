package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDtoGetItems> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    ItemDto itemUpdate(ItemDtoPatch itemDtoPatch, Long id, Long userId);

    ItemDtoById getItemById(Long id, Long userId);

    Collection<ItemDto> getSearchItems(String text);

    CommentDto addComment(Long userId, Comment comment, Long itemId);
}
