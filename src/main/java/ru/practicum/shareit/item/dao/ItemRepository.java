package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    @Query(value = "select i from Item i WHERE lower(i.name) LIKE lower(concat('%',?1,'%')) " +
            "OR lower(i.description) LIKE lower(concat('%',?1,'%')) AND i.available = TRUE")
    List<Item> getSearchItems(String text);
}
