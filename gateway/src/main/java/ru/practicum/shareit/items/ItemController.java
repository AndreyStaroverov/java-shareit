package ru.practicum.shareit.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.items.dto.Comment;
import ru.practicum.shareit.items.dto.ItemDto;
import ru.practicum.shareit.items.dto.ItemDtoPatch;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(@Autowired  ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                           @RequestParam(value = "from", required = false) @Min(1) Long from,
                                           @RequestParam(value = "size", required = false) @Min(1) Long size) {
        return itemClient.getItems(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                           @RequestBody @Valid ItemDto item) {
        return itemClient.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                           @PathVariable Long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                              @RequestBody ItemDtoPatch itemDtoPatch,
                              @PathVariable Long id) {
        return itemClient.updateItem(userId, id, itemDtoPatch);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                   @PathVariable @Positive Long id) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getSearchItems(@RequestParam(name = "text", required = false) String text,
                                              @RequestParam(value = "from", required = false) @Min(1) Long from,
                                              @RequestParam(value = "size", required = false) @Min(1) Long size) {
        return itemClient.getSearchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                 @RequestBody @Valid Comment comment,
                                 @PathVariable Long itemId) {
        return itemClient.addComment(userId, comment, itemId);
    }

}