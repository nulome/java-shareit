package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item createItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PutMapping
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody @Valid Item item) {
        return itemService.updateItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public Item deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.deleteItem(userId, itemId);
    }

    @GetMapping
    public List<Item> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> getItemByTextSearch(@RequestParam String text) {
        return itemService.getItemByTextSearch(text);
    }

    @PatchMapping("/{itemId}")
    public Item changeItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                           @RequestBody ItemDto itemDto) {
        return itemService.changeItem(userId, itemId, itemDto);
    }
}
