package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    Page<Item> findAllByOwnerId(Long ownerId, Pageable page);

    @Query(value = "select i from Item i WHERE lower(i.name) LIKE lower(concat('%',?1,'%')) " +
            "OR lower(i.description) LIKE lower(concat('%',?1,'%')) AND i.available = TRUE")
    List<Item> getSearchItems(String text);

    @Query(value = "select i from Item i WHERE lower(i.name) LIKE lower(concat('%',?1,'%')) " +
            "OR lower(i.description) LIKE lower(concat('%',?1,'%')) AND i.available = TRUE")
    Page<Item> getSearchItems(String text, Pageable page);

    @Query(value = "select * from items i " +
            "LEFT JOIN REQUESTS r ON i.REQUEST_ID  = r.ID " +
            "WHERE r.REQUESTOR_ID  = ?1", nativeQuery = true)
    List<Item> findAllByRequestorId(Long requestorId);

    @Query(value = "select * from items i " +
            "WHERE i.REQUEST_ID = ?1", nativeQuery = true)
    List<Item> findAllByRequestId(Long requestId);

    @Query(value = "select i.ID, i.NAME, i.DESCRIPTION, i.IS_AVAILABLE, i.OWNER_ID, i.REQUEST_ID " +
            "from items i " +
            "LEFT JOIN REQUESTS r ON i.REQUEST_ID  = r.ID " +
            "WHERE r.REQUESTOR_ID NOT IN (?1)", nativeQuery = true)
    List<Item> findAllNotId(Long userId);
}
