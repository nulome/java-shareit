package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;
    private final EasyRandom random = new EasyRandom();

    @BeforeEach
    void setUp() {
        itemRequestMapper = new ItemRequestMapperImpl();
    }

    @Test
    void toReqDto_whenResponseNull_thenResultNull() {
        ItemRequestReqDto reqDto = itemRequestMapper.toReqDto(null, null);
        assertNull(reqDto);
    }

    @Test
    void toItemRequestResponse_whenResponseItemReqNull_thenResultNull() {
        ItemRequest item = null;
        ItemRequestResponse reqDto = itemRequestMapper.toItemRequestResponse(item);
        assertNull(reqDto);
    }

    @Test
    void toItemRequestResponse_whenResponseItemShort_thenResultId() {
        ItemRequestShortDto item = random.nextObject(ItemRequestShortDto.class);
        ItemRequestResponse actual = itemRequestMapper.toItemRequestResponse(item);
        assertEquals(item.getId(), actual.getId());
    }

    @Test
    void toItemRequestResponse_whenResponseItemShortNull_thenResultNull() {
        ItemRequestShortDto item = null;
        ItemRequestResponse reqDto = itemRequestMapper.toItemRequestResponse(item);
        assertNull(reqDto);
    }

    @Test
    void toItemRequestShortDto_whenResponseItemReqNull_thenResultNull() {
        ItemRequest item = null;
        ItemRequestShortDto reqDto = itemRequestMapper.toItemRequestShortDto(item);
        assertNull(reqDto);
    }

    @Test
    void toItemRequest_whenRightResponse_thenResultNotNull() {
        ItemRequestReqDto itemRequestReqDto = random.nextObject(ItemRequestReqDto.class);
        itemRequestReqDto.setRequestor(null);
        ItemRequest actual = itemRequestMapper.toItemRequest(itemRequestReqDto);
        assertEquals(itemRequestReqDto.getId(), actual.getId());
    }

    @Test
    void toItemRequest_whenResponseNull_thenResultNull() {
        ItemRequestReqDto itemRequestReqDto = null;
        ItemRequest reqDto = itemRequestMapper.toItemRequest(itemRequestReqDto);
        assertNull(reqDto);
    }

    @Test
    void toItemRequest_whenRightResponse_thenResultActualId() {
        ItemRequestResponse itemRequestResponse = random.nextObject(ItemRequestResponse.class);
        ItemRequest actual = itemRequestMapper.toItemRequest(itemRequestResponse);
        assertEquals(itemRequestResponse.getId(), actual.getId());
    }

}