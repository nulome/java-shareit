package ru.practicum.shareit.item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.PatchItemRequestDto;

import static ru.practicum.shareit.related.Constants.CONTROLLER_ITEM_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@Controller
@RequestMapping(path = CONTROLLER_ITEM_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                             @RequestBody @Valid CreateItemRequestDto createItemRequestDto) {
        log.info("Creating item {}, userId={}", createItemRequestDto, userId);
        return itemClient.createItem(userId, createItemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> readItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                           @PathVariable int itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.readItem(userId, itemId);
    }

    @PutMapping
    public ResponseEntity<Object> updateItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Put item {}, userId={}", itemRequestDto, userId);
        return itemClient.updateItem(userId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId) {
        log.info("Delete item {}, userId={}", itemId, userId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByTextSearch(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                       @RequestParam(name = "text") String text,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items search with text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.getItemsByTextSearch(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId,
                                             @RequestBody PatchItemRequestDto patchItemRequestDto) {
        log.info("Patch item {}, userId={}, itemId={}", patchItemRequestDto, userId, itemId);
        return itemClient.changeItem(userId, itemId, patchItemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId,
                                                @RequestBody @Valid CreateCommentRequestDto createCommentRequestDto) {
        log.info("Creating comment {}, userId={}", createCommentRequestDto, userId);
        return itemClient.createComment(userId, itemId, createCommentRequestDto);
    }
}
