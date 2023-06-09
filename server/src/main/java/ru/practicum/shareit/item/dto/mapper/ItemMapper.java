package ru.practicum.shareit.item.dto.mapper;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoById;
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

    public static ItemDto toItemDtoRequest(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestor().getId()
        );
    }

    public static Collection<ItemDto> toItemDtoCollectionRequests(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(new ItemDto(
                    i.getId(),
                    i.getName(),
                    i.getDescription(),
                    i.getAvailable(),
                    i.getRequestor().getId()
            ));
        }
        return itemsDto;
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoById toItemDtoById(Item item) {
        return new ItemDtoById(
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

    public static Collection<ItemDtoById> toItemDtoCollectionItems(Collection<Item> items) {
        Collection<ItemDtoById> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(new ItemDtoById(
                    i.getId(),
                    i.getName(),
                    i.getDescription(),
                    i.getAvailable()
            ));
        }
        return itemsDto;
    }

}
