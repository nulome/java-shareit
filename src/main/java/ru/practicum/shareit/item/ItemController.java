package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PutMapping
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.updateItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.deleteItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByTextSearch(@RequestParam String text) {
        return itemService.getItemByTextSearch(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                           @RequestBody ItemDto itemDto) {
        return itemService.changeItem(userId, itemId, itemDto);
    }
}
