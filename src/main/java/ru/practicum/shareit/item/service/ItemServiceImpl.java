package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
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
    private final RequestRepository requestRepository;

    public ItemServiceImpl(@Autowired ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository, RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<ItemDtoById> getItems(Long userId, Long from, Long size) {
        Collection<ItemDtoById> items = new ArrayList<>();
        if (from != null && size != null) {
            Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
            Page<Item> itemsPage = itemRepository.findAllByOwnerId(userId, page);
            items = new ArrayList<>(ItemMapper.toItemDtoCollectionItems((itemsPage.get()
                    .collect(Collectors.toList()))));
        } else {
            items = ItemMapper.toItemDtoCollectionItems(itemRepository.findAllByOwnerId(userId));
        }
        for (ItemDtoById item : items) {
            item = setLastAndNextBookings(item);
            item.setComments(CommentMapper.toCommentDtoCollection(commentRepository.findByItemId(item.getId())));
        }
        return items.stream().sorted(
                Comparator.comparing(ItemDtoById::getId)).collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        try {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("Not Found User");
            }
            User user = userRepository.getById(userId);
            item.setOwner(user);

            if (itemDto.getRequestId() != null) {
                item.setRequestor(requestRepository.getById(itemDto.getRequestId()));
                return ItemMapper.toItemDtoRequest(itemRepository.save(item));
            }
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
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
    @Transactional(isolation = Isolation.READ_COMMITTED)
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
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ItemDtoById getItemById(Long id, Long userId) {
        try {
            ItemDtoById itemDtoById = ItemMapper.toItemDtoById(itemRepository.getById(id));
            if (itemRepository.getById(id).getOwner().getId().equals(userId)) {
                itemDtoById = setLastAndNextBookings(itemDtoById);
            }
            itemDtoById.setComments(CommentMapper.toCommentDtoCollection(commentRepository.findByItemId(id)));
            return itemDtoById;
        } catch (EntityNotFoundException | EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Предмета с id %s не существует", id));
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<ItemDto> getSearchItems(String text, Long from, Long size) {
        if (text.isBlank()) {
            log.warn("Отсутствует категория поиска");
            return new ArrayList<>();
        }
        if (from != null && size != null) {
            Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
            Page<Item> itemsPage = itemRepository.getSearchItems(text, page);
            return new ArrayList<>(ItemMapper.toItemDtoCollection((itemsPage.get()
                    .collect(Collectors.toList()))));
        }
        return ItemMapper.toItemDtoCollection(itemRepository.getSearchItems(text));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
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

    public ItemDtoById setLastAndNextBookings(ItemDtoById item) {
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
        return item;
    }
}
