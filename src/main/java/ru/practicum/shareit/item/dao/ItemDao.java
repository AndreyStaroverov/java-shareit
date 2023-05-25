package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotExistException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemDao {

    private HashMap<Long, Item> items = new HashMap<>();
    private Long itemIdCount = 1L;


    public Collection<Item> getSearchItems(String text) {
        ArrayList<Item> itemSearch = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                            item.getAvailable().equals(true)) {
                itemSearch.add(item);
            }
        }
        return itemSearch;
    }

    public Item getItemById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new EntityNotExistException(String.format("Item with id: %d , не существует", id));
        }
    }

    public Item updateItem(Item item, Long itemId) {
        return items.put(itemId, item);
    }

    public void deleteItem(Long itemId) {
        if (items.containsKey(itemId)) {
            items.remove(itemId);
        } else {
            throw new EntityNotExistException(String.format("Item with id: %d , не существует", itemId));
        }
    }

    public Item createItem(Item item) {
        item.setId(itemIdCount++);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Collection<Item> getItemsByOwnerId(Long userId) {
        ArrayList<Item> userItems = new ArrayList<>();
        for (Item i : items.values()) {
            if (Objects.equals(i.getOwner(), userId)) {
                userItems.add(i);
            }
        }
        return userItems;
    }
}
