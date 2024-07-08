package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class ItemMapperTest {

    private ItemMapper itemMapper;
    private final EasyRandom random = new EasyRandom();

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    void toItemResponse_whenRightResponse_thenActualId() {
        ItemRequest itemRequest = new ItemRequest(1, "text", null, null);
        Item item = new Item(1, "text", "text", true, null, itemRequest);

        ItemResponse itemResponseActual = itemMapper.toItemResponse(item);
        assertEquals(1, itemResponseActual.getId());
        assertEquals(1, itemResponseActual.getRequestId());
    }

    @Test
    void toItemDto_whenRightResponse_thenActualId() {
        Item item = random.nextObject(Item.class);
        item.setRequest(null);
        ItemDto actual = itemMapper.toItemDto(item);
        assertEquals(item.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    void toItemDto_whenRightResponse_thenActualName() {
        CreateItemRequestDto create = random.nextObject(CreateItemRequestDto.class);
        User user = random.nextObject(User.class);
        ItemRequest itemRequest = null;

        ItemDto actual = itemMapper.toItemDto(create, user, itemRequest);
        assertEquals(create.getName(), actual.getName());
    }

    @Test
    void toItemDto_whenResponseCreateNull_thenResultNull() {
        Item item = null;
        ItemDto actual = itemMapper.toItemDto(item);
        assertNull(actual);
    }

    @Test
    void toItemDto_whenResponseItemRequestDtoNull_thenResultNull() {
        ItemRequestDto itemRequestDto = null;
        ItemDto actual = itemMapper.toItemDto(itemRequestDto);
        assertNull(actual);
    }

    @Test
    void toItemDto_whenResponseNull_thenResultNull() {
        ItemDto actual = itemMapper.toItemDto(null, null, null);
        assertNull(actual);
    }

    @Test
    void toItemResponse_whenResponseItemNull_thenResultNull() {
        Item item = null;
        ItemResponse actual = itemMapper.toItemResponse(item);
        assertNull(actual);
    }

    @Test
    void toComment_whenResponseNull_thenResultNull() {
        Comment actual = itemMapper.toComment(null);
        assertNull(actual);
    }

    @Test
    void toCommentResponse_whenResponseNull_thenResultNull() {
        Comment comment = null;
        CommentResponse actual = itemMapper.toCommentResponse(comment);
        assertNull(actual);
    }

    @Test
    void toItem_whenResponseNull_thenResultNull() {
        Item item = itemMapper.toItem(null);
        assertNull(item);
    }

    @Test
    void toInfoBookingByItemDto_whenResponseNull_thenResultNull() {
        InfoBookingByItemDto item = itemMapper.toInfoBookingByItemDto(null);
        assertNull(item);
    }

    @Test
    void toCommentDto_whenResponseNull_thenResultNull() {
        CommentDto commentDto = itemMapper.toCommentDto(null, null, null);
        assertNull(commentDto);
    }

    @Test
    void toCommentDto_whenResponseCommentCreate_thenResultDto() {
        CreateCommentRequestDto create = new CreateCommentRequestDto("text");
        CommentDto commentDto = itemMapper.toCommentDto(create, null, null);
        assertEquals(create.getText(), commentDto.getText());
    }

}