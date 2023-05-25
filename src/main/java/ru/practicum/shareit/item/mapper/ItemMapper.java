package ru.practicum.shareit.item.mapper;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Collection<ItemDto> toItemDtoCollection(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(new ItemDto(
                    i.getId(),
                    i.getName(),
                    i.getDescription(),
                    i.getAvailable()
            ));
        }
        return itemsDto;
    }

}
