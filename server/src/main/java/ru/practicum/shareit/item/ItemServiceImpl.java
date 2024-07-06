package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.CustomValueException;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CommentShortDto;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.related.UtilityClasses.createPageRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemResponse createItem(Integer userId, CreateItemRequestDto createItemRequestDto) {
        User user = checkUserInDB(userId);
        ItemRequest itemRequest = null;
        if (createItemRequestDto.getRequestId() != null) {
            itemRequest = checkRequestInDB(createItemRequestDto.getRequestId());
        }
        ItemDto itemDto = itemMapper.toItemDto(createItemRequestDto, user, itemRequest);

        Item item = itemMapper.toItem(itemDto);
        item = itemRepository.save(item);
        return itemMapper.toItemResponse(item);
    }


    @Override
    public ItemWithDateBookingResponse readItem(int userId, Integer itemId) {
        Item item = checkItemInDB(itemId);
        ItemWithDateBookingResponse itemResponse = itemMapper.toItemWithDateBookingResponse(item);
        List<Booking> bookingList = new ArrayList<>();
        if (userId == item.getOwner().getId()) {
            bookingList = bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(itemId, StatusBooking.APPROVED);
        }
        ItemWithDateBookingResponse itemWithDateBookingResponse = updateItemToListBooking(bookingList, itemResponse);
        enrichCommentsFromDB(List.of(itemWithDateBookingResponse));
        return itemWithDateBookingResponse;
    }

    @Override
    public ItemResponse updateItem(Integer userId, ItemRequestDto itemRequestDto) {
        ItemDto itemDto = itemMapper.toItemDto(itemRequestDto);
        Item item = checkItemInDB(itemDto.getId());
        verificationOfCreator(userId, item);
        itemDto.setOwner(userMapper.toUserDto(item.getOwner()));
        if (item.getRequest() != null) {
            itemDto.setRequest(itemRequestMapper.toItemRequestResponse(item.getRequest()));
        }

        item = itemMapper.toItem(itemDto);
        item = itemRepository.save(item);
        return itemMapper.toItemResponse(item);
    }

    @Override
    public void deleteItem(Integer userId, Integer itemId) {
        Item item = itemRepository.getReferenceById(itemId);
        verificationOfCreator(userId, item);

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemWithDateBookingResponse> getItems(int userId, Integer from, Integer size) {
        Pageable pageable = createPageRequest(from, size);
        List<Item> listItem = itemRepository.findAllByOwnerIdOrderById(userId, pageable).getContent();
        List<Booking> bookingList = bookingRepository.getBookingsByOwnerItemAndStatus(
                userId, StatusBooking.APPROVED, null).getContent();
        List<ItemWithDateBookingResponse> listResponse = listItem.stream()
                .map(itemMapper::toItemWithDateBookingResponse)
                .collect(toList());

        updateItemResponseByInfoBooking(listResponse, bookingList);
        enrichCommentsFromDB(listResponse);
        return listResponse;
    }

    @Override
    public ItemResponse changeItem(Integer userId, Integer itemId, PatchItemRequestDto patchItemRequestDto) {
        Item item = checkItemInDB(itemId);
        verificationOfCreator(userId, item);
        ItemDto itemDto = itemMapper.toItemDto(item);

        changeByPatchDto(itemDto, patchItemRequestDto);
        item = itemMapper.toItem(itemDto);
        item = itemRepository.save(item);
        return itemMapper.toItemResponse(item);
    }

    @Override
    public List<ItemResponse> getItemsByTextSearch(Integer userId, String text, Integer from, Integer size) {
        checkUserInDB(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = createPageRequest(from, size);
        List<Item> itemList = itemRepository.findItemsByTextSearch(text, pageable).getContent();
        return itemList.stream()
                .map(itemMapper::toItemResponse)
                .collect(toList());
    }

    @Override
    public CommentResponse createComment(int userId, Integer itemId, CreateCommentRequestDto createCommentRequestDto) {
        Booking booking = checkAppropriateBookingInDB(userId, itemId);
        CommentDto commentDto = itemMapper.toCommentDto(createCommentRequestDto, booking.getBooker(), booking.getItem());

        Comment comment = itemMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        return itemMapper.toCommentResponse(comment);
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

    private void updateItemResponseByInfoBooking(List<ItemWithDateBookingResponse> listResponse, List<Booking> bookingList) {
        for (ItemWithDateBookingResponse item : listResponse) {
            int itemId = item.getId();
            List<Booking> bookingFilter = bookingList.stream()
                    .filter(booking -> booking.getItem().getId() == itemId)
                    .collect(toList());
            updateItemToListBooking(bookingFilter, item);
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

        item.setLastBooking(bookingLast == null ? null : itemMapper.toInfoBookingByItemDto(bookingLast));
        item.setNextBooking(bookingNext == null ? null : itemMapper.toInfoBookingByItemDto(bookingNext));
        return item;
    }

    private void enrichCommentsFromDB(List<ItemWithDateBookingResponse> listResponse) {
        List<Integer> listItemId = listResponse.stream()
                .map(ItemWithDateBookingResponse::getId)
                .collect(toList());

        Map<Integer, List<CommentShortDto>> comments = commentRepository.findAllByItemIdIn(listItemId).stream()
                .collect(groupingBy(CommentShortDto::getItemId, toList()));

        for (ItemWithDateBookingResponse itemWithDateBookingResponse : listResponse) {
            List<CommentResponse> listResult = comments.getOrDefault(itemWithDateBookingResponse.getId(), new ArrayList<>()).stream()
                    .map(itemMapper::toCommentResponse)
                    .collect(toList());
            itemWithDateBookingResponse.setComments(listResult);
        }
    }

    private Item checkItemInDB(int itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено для item: " + itemId));
    }

    private User checkUserInDB(int userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено user: " + userId));
    }

    private ItemRequest checkRequestInDB(int requestId) {
        return itemRequestRepository.getItemRequestById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено для request: " + requestId));
    }

    private Booking checkAppropriateBookingInDB(int userId, int itemId) {
        return bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(userId, itemId,
                        ZonedDateTime.now(), StatusBooking.APPROVED.toString())
                .orElseThrow(() -> new CustomValueException("Завершенного бронирования для Item " + itemId +
                        " по User " + userId + " - не найдено"));
    }
}
