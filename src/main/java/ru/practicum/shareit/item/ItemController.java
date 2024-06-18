package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                   @RequestBody @Valid CreateItemRequestDto createItemRequestDto) {
        return itemService.createItem(userId, createItemRequestDto);
    }

    @Transactional
    @GetMapping("/{itemId}")
    public ItemWithDateBookingResponse readItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @PathVariable int itemId) {
        return itemService.readItem(userId, itemId);
    }

    @PutMapping
    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                   @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemService.updateItem(userId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping
    public List<ItemWithDateBookingResponse> getItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                                      @RequestParam(required = false) Integer from,
                                                      @RequestParam(required = false) Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> getItemsByTextSearch(@RequestParam String text,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return itemService.getItemsByTextSearch(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse changeItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                                   @RequestBody PatchItemRequestDto patchItemRequestDto) {
        return itemService.changeItem(userId, itemId, patchItemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                                      @RequestBody @Valid CreateCommentRequestDto createCommentRequestDto) {
        return itemService.createComment(userId, itemId, createCommentRequestDto);
    }
}
