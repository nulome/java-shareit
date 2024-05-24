package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface ItemService {
    Item createItem(Integer userId, @Valid ItemDto itemDto);

    Item updateItem(Integer userId, Item item);

    Item deleteItem(Integer userId, Integer itemId);

    List<Item> getItems(Integer userId);

    Item getItem(Integer itemId);

    Item changeItem(Integer userId, Integer itemId, ItemDto itemDto);

    List<Item> getItemByTextSearch(String text);
}
