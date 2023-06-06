package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(@Autowired ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                           @RequestBody @Valid ItemDto item) {
        if (userId == null) {
            throw new NotOwnerException("Отсутствует владелец");
        }
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                              @RequestBody ItemDtoPatch itemDtoPatch,
                              @PathVariable Long id) {
        return itemService.itemUpdate(itemDtoPatch, id, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@PathVariable @Positive Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getSearchItems(@RequestParam(name = "text", required = false) String text) {
        return itemService.getSearchItems(text);
    }

}
