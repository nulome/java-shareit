package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        log.info("Получен запрос Post /items - {} пользователя {}", itemDto.getName(), userId);
        User user = checkAndReceiptUserInDataBase(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item = itemRepository.createItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer userId, ItemDto itemDto) {
        log.info("Получен запрос Put /items - {} пользователя {}", itemDto.getName(), userId);
        Item item = ItemMapper.toItem(itemDto);
        checkAndReceiptItemInDataBase(item.getId());
        verificationOfCreator(userId, item);
        item = itemRepository.updateItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto deleteItem(Integer userId, Integer itemId) {
        log.info("Получен запрос Delete /items/{} пользователя {}", itemId, userId);
        Item item = checkAndReceiptItemInDataBase(itemId);
        verificationOfCreator(userId, item);
        itemRepository.deleteItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        log.info("Получен запрос Get /items пользователя {}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        List<Item> itemList = itemRepository.getItems(user);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        log.info("Получен запрос Get /items/{}", itemId);
        Item item = checkAndReceiptItemInDataBase(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto changeItem(Integer userId, Integer itemId, ItemDto itemDto) {
        log.info("Получен запрос Patch /items/{} пользователя {}", itemId, userId);
        Item item = checkAndReceiptItemInDataBase(itemId);
        verificationOfCreator(userId, item);
        changeItemByDto(item, itemDto);
        item = itemRepository.updateItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemByTextSearch(String text) {
        log.info("Получен запрос Get /search?text={}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository.getItemByTextSearch(text);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void changeItemByDto(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && itemDto.getAvailable() != item.getAvailable()) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    private void verificationOfCreator(int userId, Item item) {
        log.trace("Проверка подлинности создателя Item: {}", item.getName());
        if (userId != item.getOwner().getId()) {
            log.error("Пользователь {} не является создателем Item {}", userId, item.getName());
            throw new UnknownValueException("Запрет доступа пользователю: " + userId);
        }
    }

    private Item checkAndReceiptItemInDataBase(Integer itemId) {
        log.trace("Проверка Item: {} в базе", itemId);
        try {
            return itemRepository.getItem(itemId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не найдено значение по itemId: {} \n {}", itemId, e.getMessage());
            throw new UnknownValueException("Передан не верный itemId: " + itemId);
        }
    }

    private User checkAndReceiptUserInDataBase(Integer userId) {
        log.trace("Проверка User: {} в базе", userId);
        try {
            return userRepository.getUser(userId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка в запросе к базе данных. Не найдено значение по userId: {} \n {}", userId, e.getMessage());
            throw new UnknownValueException("Передан не верный userId: " + userId);
        }
    }
}
