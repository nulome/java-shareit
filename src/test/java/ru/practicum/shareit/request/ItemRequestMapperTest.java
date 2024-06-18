package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
        itemRequestMapper = new ItemRequestMapperImpl();
    }

    @Test
    void toReqDto() {
        ItemRequestReqDto reqDto = itemRequestMapper.toReqDto(null, null);
        assertNull(reqDto);
    }

    @Test
    void toItemRequest() {
        ItemRequest reqDto = itemRequestMapper.toItemRequest(null);
        assertNull(reqDto);
    }

}