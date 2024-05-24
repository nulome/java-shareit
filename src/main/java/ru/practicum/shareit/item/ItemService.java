package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Validated
public interface ItemService {
    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, ItemDto itemDto);

    ItemDto deleteItem(Integer userId, Integer itemId);

    List<ItemDto> getItems(Integer userId);

    ItemDto getItem(Integer itemId);

    ItemDto changeItem(Integer userId, Integer itemId, ItemDto itemDto);

    List<ItemDto> getItemByTextSearch(String text);
}
