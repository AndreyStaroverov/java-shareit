package ru.practicum.shareit.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.items.dto.Comment;
import ru.practicum.shareit.items.dto.ItemDto;
import ru.practicum.shareit.items.dto.ItemDtoPatch;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(Long userId, Long from, Long size) {
        if (from != null && size != null) {
            Map<String, Object> parameters = Map.of(
                    "from", from,
                    "size", size
            );
            return get("?from={from}&size={size}", userId, parameters);
        } else {
            return get("", userId);
        }
    }

    public ResponseEntity<Object> addNewItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDtoPatch itemDtoPatch) {
        return patch("/" + itemId, userId, itemDtoPatch);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getSearchItems(String text, Long from, Long size) {
        if (from != null && size != null) {
            return get("/search?text=" + text + "&from=" + from + "&size=" + size);
        } else {
            return get("/search?text=" + text);
        }
    }

    public ResponseEntity<Object> addComment(Long userId, Comment comment, Long itemId) {
        return post("/" + itemId + "/comment", userId, comment);
    }


}
