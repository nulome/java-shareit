package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_ITEM_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(CONTROLLER_ITEM_PATH)
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                   @RequestBody CreateItemRequestDto createItemRequestDto) {
        log.info("Получен запрос Post {} - {} пользователя {}", CONTROLLER_ITEM_PATH, createItemRequestDto.getName(), userId);
        return itemService.createItem(userId, createItemRequestDto);
    }

    @Transactional
    @GetMapping("/{itemId}")
    public ItemWithDateBookingResponse readItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                @PathVariable int itemId) {
        log.info("Получен запрос Get {}/{}", CONTROLLER_ITEM_PATH, itemId);
        return itemService.readItem(userId, itemId);
    }

    @PutMapping
    public ItemResponse updateItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                   @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос Put {} - {} пользователя {}", CONTROLLER_ITEM_PATH, itemRequestDto.getName(), userId);
        return itemService.updateItem(userId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId) {
        log.info("Получен запрос Delete {}/{} пользователя {}", CONTROLLER_ITEM_PATH, itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping
    public List<ItemWithDateBookingResponse> getItems(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                      @RequestParam Integer from,
                                                      @RequestParam Integer size) {
        log.info("Получен запрос Get {} пользователя {}", CONTROLLER_ITEM_PATH, userId);
        return itemService.getItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse changeItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId,
                                   @RequestBody PatchItemRequestDto patchItemRequestDto) {
        log.info("Получен запрос Patch {}/{} пользователя {}", CONTROLLER_ITEM_PATH, itemId, userId);
        return itemService.changeItem(userId, itemId, patchItemRequestDto);
    }

    @GetMapping("/search")
    public List<ItemResponse> getItemsByTextSearch(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                   @RequestParam String text,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        log.info("Получен запрос Get {}/search?text={}", CONTROLLER_ITEM_PATH, text);
        return itemService.getItemsByTextSearch(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId,
                                         @RequestBody CreateCommentRequestDto createCommentRequestDto) {
        log.info("Получен запрос POST {}/{}/comment от User {}", CONTROLLER_ITEM_PATH, itemId, userId);
        return itemService.createComment(userId, itemId, createCommentRequestDto);
    }
}
