package ru.practicum.shareit.items.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.dto.ItemRequest;
import ru.practicum.shareit.user.dto.User;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Positive
    private Long id;

    private String name;

    private String description;

    private Boolean available;
    private User owner;
    private ItemRequest requestor;

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}