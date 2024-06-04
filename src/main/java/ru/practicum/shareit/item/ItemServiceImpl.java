package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.CustomValueException;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.item.commentDto.CommentMapper;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Override
    public ItemResponse createItem(Integer userId, CreateItemRequestDto createItemRequestDto) {
        log.info("Получен запрос Post /items - {} пользователя {}", createItemRequestDto.getName(), userId);
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено user: " + userId));
        ItemDto itemDto = modelMapper.map(createItemRequestDto, ItemDto.class);
        itemDto.setOwner(user);

        Item item = modelMapper.map(itemDto, Item.class);
        item = itemRepository.save(item);
        return modelMapper.map(item, ItemResponse.class);
    }


    @Override
    public ItemWithDateBookingResponse readItem(int userId, Integer itemId) {
        log.info("Получен запрос Get /items/{}", itemId);
        Item item = checkItemInDB(itemId);
        ItemWithDateBookingResponse itemResponse = ItemMapper.toItemDto(item);
        List<Booking> bookingList = new ArrayList<>();
        if (userId == item.getOwner().getId()) {
            bookingList = bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(itemId, StatusBooking.APPROVED);
        }
        return updateItemToListBooking(bookingList, itemResponse);
    }

    @Override
    public ItemResponse updateItem(Integer userId, ItemRequestDto itemRequestDto) {
        log.info("Получен запрос Put /items - {} пользователя {}", itemRequestDto.getName(), userId);
        ItemDto itemDto = modelMapper.map(itemRequestDto, ItemDto.class);
        Item item = checkItemInDB(itemDto.getId());
        verificationOfCreator(userId, item);
        itemDto.setOwner(item.getOwner());
        if (item.getRequest() != null) {
            itemDto.setRequest(item.getRequest());
        }

        item = modelMapper.map(itemDto, Item.class);
        item = itemRepository.save(item);
        return modelMapper.map(item, ItemResponse.class);
    }

    @Override
    public void deleteItem(Integer userId, Integer itemId) {
        log.info("Получен запрос Delete /items/{} пользователя {}", itemId, userId);
        Item item = itemRepository.getReferenceById(itemId);
        verificationOfCreator(userId, item);

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemWithDateBookingResponse> getItems(int userId) {
        log.info("Получен запрос Get /items пользователя {}", userId);
        List<Item> listItem = itemRepository.findAllByOwnerIdOrderById(userId);
        List<Booking> bookingList = bookingRepository.getBookingsByOwnerItemAndStatus(userId, StatusBooking.APPROVED.toString());
        List<ItemWithDateBookingResponse> listResponse = listItem.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        for (ItemWithDateBookingResponse item : listResponse) {
            int itemId = item.getId();
            List<Booking> bookingFilter = bookingList.stream()
                    .filter(booking -> booking.getItem().getId() == itemId)
                    .collect(Collectors.toList());
            updateItemToListBooking(bookingFilter, item);
        }
        return listResponse;
    }


    @Override
    public ItemResponse changeItem(Integer userId, Integer itemId, PatchItemRequestDto patchItemRequestDto) {
        log.info("Получен запрос Patch /items/{} пользователя {}", itemId, userId);
        Item item = checkItemInDB(itemId);
        verificationOfCreator(userId, item);
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);

        changeByPatchDto(itemDto, patchItemRequestDto);
        item = modelMapper.map(itemDto, Item.class);
        item = itemRepository.save(item);
        return modelMapper.map(item, ItemResponse.class);
    }

    @Override
    public List<ItemResponse> getItemsByTextSearch(String text) {
        log.info("Получен запрос Get /search?text={}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository.findItemsByTextSearch(text, text);
        return itemList.stream()
                .map(item -> modelMapper.map(item, ItemResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse createComment(int userId, Integer itemId, CreateCommentRequestDto createCommentRequestDto) {
        log.info("Получен запрос POST /items/{}/comment от User {}", itemId, userId);
        Booking booking = checkAppropriateBookingInDB(userId, itemId);
        Comment comment = modelMapper.map(createCommentRequestDto, Comment.class);
        comment.setCreated(ZonedDateTime.now());
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }


    private void changeByPatchDto(ItemDto itemDto, PatchItemRequestDto patchItemRequestDto) {
        if (patchItemRequestDto.getName() != null) {
            itemDto.setName(patchItemRequestDto.getName());
        }
        if (patchItemRequestDto.getDescription() != null) {
            itemDto.setDescription(patchItemRequestDto.getDescription());
        }
        if (patchItemRequestDto.getAvailable() != null) {
            itemDto.setAvailable(patchItemRequestDto.getAvailable());
        }
    }

    private void verificationOfCreator(int userId, Item item) {
        log.trace("Проверка подлинности создателя Item: {}", item.getName());
        if (userId != item.getOwner().getId()) {
            log.error("Пользователь {} не является создателем Item {}", userId, item.getName());
            throw new UnknownValueException("Запрет доступа пользователю: " + userId);
        }
    }

    private ItemWithDateBookingResponse updateItemToListBooking(List<Booking> bookingList, ItemWithDateBookingResponse item) {
        if (bookingList == null || bookingList.isEmpty()) {
            return item;
        }
        Booking bookingLast = null;
        Booking bookingNext = null;
        if (bookingList.size() == 1) {
            bookingLast = bookingList.get(0);
//            bookingNext = bookingList.get(0);
        } else {
            ZonedDateTime nowTime = ZonedDateTime.now();
            for (Booking booking : bookingList) {
                if (booking.getEnd().isBefore(nowTime)) {
                    bookingLast = bookingLast == null ? booking :
                            bookingLast.getEnd().isBefore(booking.getEnd()) ? booking : bookingLast;
                }
                if (booking.getStart().isAfter(nowTime)) {
                    bookingNext = bookingNext == null ? booking :
                            bookingNext.getStart().isAfter(booking.getStart()) ? booking : bookingNext;
                }
            }
        }

        item.setLastBooking(bookingLast == null ? null : modelMapper.map(bookingLast, InfoBookingByItemDto.class));
        item.setNextBooking(bookingNext == null ? null : modelMapper.map(bookingNext, InfoBookingByItemDto.class));
        return item;
    }

    private Item checkItemInDB(int itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено для item: " + itemId));
    }

    private Booking checkAppropriateBookingInDB(int userId, int itemId) {
        return bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(userId, itemId,
                        ZonedDateTime.now(), StatusBooking.APPROVED.toString())
                .orElseThrow(() -> new CustomValueException("Завершенного бронирования для Item " + itemId +
                        " по User " + userId + " - не найдено"));
    }
}
