package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.PatchItemRequestDto;

import java.util.Map;

import static ru.practicum.shareit.related.Constants.CONTROLLER_ITEM_PATH;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = CONTROLLER_ITEM_PATH;

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(int userId, CreateItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> readItem(int userId, Integer itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(int userId, ItemRequestDto requestDto) {
        return put("", userId, requestDto);
    }

    public ResponseEntity<Object> deleteItem(int userId, Integer itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(int userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemsByTextSearch(int userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> changeItem(int userId, Integer itemId, PatchItemRequestDto patchDto) {
        return patch("/" + itemId, userId, patchDto);
    }

    public ResponseEntity<Object> createComment(int userId, Integer itemId, CreateCommentRequestDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }

}
