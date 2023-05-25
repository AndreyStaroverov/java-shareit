package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    public ItemServiceImpl(@Autowired ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        return ItemMapper.toItemDtoCollection(itemDao.getItemsByOwnerId(userId));
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        try {
            userDao.getUserById(userId);
        } catch (EntityNotExistException e) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemDao.createItem(item));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (!itemDao.getItemById(itemId).getOwner().equals(userId)) {
            throw new NotOwnerException("Пользователь не является владельцем вещи");
        }
        itemDao.deleteItem(itemId);
    }

    @Override
    public ItemDto itemUpdate(ItemDtoPatch itemDtoPatch, Long itemId, Long userId) {
        Item item = itemDao.getItemById(itemId);
        if (!itemDao.getItemById(itemId).getOwner().equals(userId)) {
            throw new NotOwnerException("Пользователь не является владельцем вещи");
        }
        if (itemDtoPatch.getName() != null) {
            item.setName(itemDtoPatch.getName());
        }
        if (itemDtoPatch.getDescription() != null) {
            item.setDescription(itemDtoPatch.getDescription());
        }
        if (itemDtoPatch.getAvailable() != null) {
            item.setAvailable(itemDtoPatch.getAvailable());
        }
        return ItemMapper.toItemDto(itemDao.updateItem(item, itemId));
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemDao.getItemById(id));
    }

    @Override
    public Collection<ItemDto> getSearchItems(String text) {
        if (text.isBlank()) {
            log.warn("Отсутствует категория поиска");
            return new ArrayList<>();
        }
        return ItemMapper.toItemDtoCollection(itemDao.getSearchItems(text));
    }
}
