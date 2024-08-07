package ru.practicum.shareit.item;

import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(Integer userId, CreateItemRequestDto createItemRequestDto);

    ItemWithDateBookingResponse readItem(int userId, Integer itemId);

    ItemResponse updateItem(Integer userId, ItemRequestDto itemRequestDto);

    void deleteItem(Integer userId, Integer itemId);

    List<ItemWithDateBookingResponse> getItems(int userId, Integer from, Integer size);

    ItemResponse changeItem(Integer userId, Integer itemId, PatchItemRequestDto patchItemRequestDto);

    List<ItemResponse> getItemsByTextSearch(Integer userId, String text, Integer from, Integer size);

    CommentResponse createComment(int userId, Integer itemId, CreateCommentRequestDto createCommentRequestDto);
}
