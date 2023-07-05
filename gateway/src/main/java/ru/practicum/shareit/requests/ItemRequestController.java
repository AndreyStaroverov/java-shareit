package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(@Autowired ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(userId,itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequest(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequests(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                            @RequestParam(value = "size", defaultValue = "10") @Min(0) int size) {
        return itemRequestClient.getFromSize(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                         @PathVariable(value = "requestId") @Positive Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }

}