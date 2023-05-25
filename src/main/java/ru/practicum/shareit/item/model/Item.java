package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    public Item(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    private Long owner;
    @NotNull
    private ItemRequest request;

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
