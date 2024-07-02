package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
        return itemService.createItem(userId, createItemRequestDto);
    }

    @Transactional
    @GetMapping("/{itemId}")
    public ItemWithDateBookingResponse readItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                @PathVariable int itemId) {
        return itemService.readItem(userId, itemId);
    }

    @PutMapping
    public ItemResponse updateItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                   @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.updateItem(userId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping
    public List<ItemWithDateBookingResponse> getItems(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                      @RequestParam Integer from,
                                                      @RequestParam Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> getItemsByTextSearch(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                   @RequestParam String text,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        return itemService.getItemsByTextSearch(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse changeItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId,
                                   @RequestBody PatchItemRequestDto patchItemRequestDto) {
        return itemService.changeItem(userId, itemId, patchItemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int itemId,
                                         @RequestBody CreateCommentRequestDto createCommentRequestDto) {
        return itemService.createComment(userId, itemId, createCommentRequestDto);
    }
}
