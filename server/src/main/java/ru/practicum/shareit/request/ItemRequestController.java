package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(@Autowired ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(itemRequestDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "from", defaultValue = "0") int from,
                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return itemRequestService.getFromSize(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @PathVariable(value = "requestId") Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

}
