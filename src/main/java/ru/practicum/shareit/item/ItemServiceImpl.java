package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Item createItem(Integer userId, ItemDto itemDto) {
        log.info("Получен запрос Post /items - {} пользователя {}", itemDto.getName(), userId);
        User user = checkAndReceiptUserInDataBase(userId);
        Item item = modelMapper.map(itemDto, Item.class);
        item.setOwner(user);
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItem(Integer userId, Item item) {
        log.info("Получен запрос Put /items - {} пользователя {}", item.getName(), userId);
        verificationOfCreator(userId, checkAndReceiptItemInDataBase(item.getId()));
        return itemRepository.updateItem(item);
    }

    @Override
    public Item deleteItem(Integer userId, Integer itemId) {
        log.info("Получен запрос Delete /items/{} пользователя {}", itemId, userId);
        Item item = checkAndReceiptItemInDataBase(itemId);
        verificationOfCreator(userId, item);
        itemRepository.deleteItem(itemId);
        return item;
    }

    @Override
    public List<Item> getItems(Integer userId) {
        log.info("Получен запрос Get /items пользователя {}", userId);
        User user = checkAndReceiptUserInDataBase(userId);
        return itemRepository.getItems(user);
    }

    @Override
    public Item getItem(Integer itemId) {
        log.info("Получен запрос Get /items/{}", itemId);
        return checkAndReceiptItemInDataBase(itemId);
    }

    @Override
    public Item changeItem(Integer userId, Integer itemId, ItemDto itemDto) {
        log.info("Получен запрос Patch /items/{} пользователя {}", itemId, userId);
        Item item = getItem(itemId);
        verificationOfCreator(userId, item);
        changeItemByDto(item, itemDto);
        Item item1 = itemRepository.updateItem(item);
        return item1;
    }

    @Override
    public List<Item> getItemByTextSearch(String text) {
        log.info("Получен запрос Get /search?text={}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getItemByTextSearch(text);
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
