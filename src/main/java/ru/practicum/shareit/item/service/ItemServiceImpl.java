package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusOfBooking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingToDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(@Autowired ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDtoGetItems> getItems(Long userId) {
        Collection<ItemDtoGetItems> items = new ArrayList<>();
        items = ItemMapper.toItemDtoCollectionGetItems(itemRepository.findAllByOwnerId(userId));
        for (ItemDtoGetItems item : items) {
            List<Booking> bookingsPast = bookingRepository
                    .findByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(item.getId(),
                    StatusOfBooking.APPROVED,
                    Timestamp.valueOf(LocalDateTime.now()));
            List<Booking> bookingsNext = new ArrayList<>();
            if (bookingsPast.size() != 0) {
                item.setLastBooking(BookingToDto.toBookingDto(bookingsPast.get(0)));
                bookingsNext = bookingRepository.findByItemIdAndStatusAndStartIsAfterOrderByEndAsc(item.getId(),
                        StatusOfBooking.APPROVED,
                        bookingsPast.get(0).getEnd());
            } else {
                bookingsNext = bookingRepository.findByItemIdAndStatusAndStartIsAfterOrderByEndAsc(item.getId(),
                        StatusOfBooking.APPROVED,
                        Timestamp.valueOf(LocalDateTime.now()));
            }
            if (bookingsNext.size() != 0) {
                item.setNextBooking(BookingToDto.toBookingDto(bookingsNext.get(0)));
            }
            item.setComments(CommentMapper.toCommentDtoCollection(commentRepository.findByItemId(item.getId())));
        }
        return items.stream().sorted(
                Comparator.comparing(ItemDtoGetItems::getId)).collect(Collectors.toList());
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
    public ItemDtoById getItemById(Long id, Long userId) {
        try {
            ItemDtoById itemDtoById = ItemMapper.toItemDtoById(itemRepository.getById(id));
            if (itemRepository.getById(id).getOwner().getId().equals(userId)) {
                List<Booking> bookingsPast = bookingRepository.findByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(id,
                        StatusOfBooking.APPROVED,
                        Timestamp.valueOf(LocalDateTime.now()));
                List<Booking> bookingsNext = new ArrayList<>();
                if (bookingsPast.size() != 0) {
                    itemDtoById.setLastBooking(BookingToDto.toBookingDto(bookingsPast.get(0)));
                    bookingsNext = bookingRepository.findByItemIdAndStatusAndStartIsAfterOrderByEndAsc(id,
                            StatusOfBooking.APPROVED,
                            bookingsPast.get(0).getEnd());
                } else {
                    bookingsNext = bookingRepository.findByItemIdAndStatusAndStartIsAfterOrderByEndAsc(id,
                            StatusOfBooking.APPROVED,
                            Timestamp.valueOf(LocalDateTime.now()));
                }
                if (bookingsNext.size() != 0) {
                    itemDtoById.setNextBooking(BookingToDto.toBookingDto(bookingsNext.get(0)));
                }
            }
            itemDtoById.setComments(CommentMapper.toCommentDtoCollection(commentRepository.findByItemId(id)));
            return itemDtoById;
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

    @Override
    public CommentDto addComment(Long userId, Comment comment, Long itemId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not found");
        }
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item Not Found");
        }
        if (bookingRepository.findByItemId(itemId, userId, Timestamp.valueOf(LocalDateTime.now())).size() == 0) {
            throw new BadRequestException("This user not share this item");
        }
        comment.setDateCreated(LocalDateTime.now());
        comment.setItem(itemRepository.getById(itemId));
        comment.setUser(userRepository.getById(userId));
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
