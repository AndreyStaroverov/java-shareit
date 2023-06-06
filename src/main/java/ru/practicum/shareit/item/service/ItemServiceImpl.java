package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(@Autowired ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getItems(Long userId) {
        return ItemMapper.toItemDtoCollection(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        try {
           boolean check = userRepository.existsById(userId);
           if (check) {
               User user = userRepository.getById(userId);
               item.setOwner(user);
           } else {
               throw new EntityNotFoundException("Not Found User");
           }
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        try {
            if (!itemRepository.getById(itemId).getOwner().getId().equals(userId)) {
                throw new NotOwnerException("Пользователь не является владельцем вещи");
            }
            itemRepository.deleteById(itemId);
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Предмета с id %s не существует", itemId));
        }
    }

    @Override
    public ItemDto itemUpdate(ItemDtoPatch itemDtoPatch, Long itemId, Long userId) {
        Item item = itemRepository.getById(itemId);
        if (!itemRepository.getById(itemId).getOwner().getId().equals(userId)) {
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long id) {
        try {
            return ItemMapper.toItemDto(itemRepository.getById(id));
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Предмета с id %s не существует", id));
        }

    }

    @Override
    public Collection<ItemDto> getSearchItems(String text) {
        if (text.isBlank()) {
            log.warn("Отсутствует категория поиска");
            return new ArrayList<>();
        }
        return ItemMapper.toItemDtoCollection(itemRepository.getSearchItems(text));
    }
}
