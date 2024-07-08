package ru.practicum.shareit.item;

import javax.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.CustomValueException;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemRequestMapper itemRequestMapper;

    private final EasyRandom random = new EasyRandom();

    private ItemResponse itemResponse;
    ItemDto itemDto;
    Item item;
    private ItemWithDateBookingResponse itemWithDateBookingResponse;
    private User user;
    private final int userId = 1;
    private final int itemId = 1;


    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository, itemMapper, userMapper, itemRequestMapper);
        user = random.nextObject(User.class);
        itemResponse = random.nextObject(ItemResponse.class);
        itemDto = random.nextObject(ItemDto.class);
        item = random.nextObject(Item.class);
        itemWithDateBookingResponse = random.nextObject(ItemWithDateBookingResponse.class);

    }

    @Test
    void createItem_whenRightRequest_thenVerifyMethod() {
        CreateItemRequestDto createItemRequestDto = random.nextObject(CreateItemRequestDto.class);
        createItemRequestDto.setRequestId(null);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItemDto(createItemRequestDto, user, null))
                .thenReturn(itemDto);
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);

        itemService.createItem(userId, createItemRequestDto);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_whenNotUserInDB_thenThrows() {
        CreateItemRequestDto createItemRequestDto = random.nextObject(CreateItemRequestDto.class);
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(userId, createItemRequestDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItem_whenNotItemRequestInDB_thenThrows() {
        CreateItemRequestDto createItemRequestDto = random.nextObject(CreateItemRequestDto.class);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(userId, createItemRequestDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void readItem_whenItemIdWithBookingLastNext_thenItemActualLastNextWithVerifyMethod() {
        Booking bookingLast = new Booking();
        bookingLast.setEnd(ZonedDateTime.now().minusYears(20));
        bookingLast.setStart(ZonedDateTime.now().minusYears(21));
        bookingLast.setId(20);

        InfoBookingByItemDto infoLast = new InfoBookingByItemDto();
        infoLast.setId(bookingLast.getId());

        Booking bookingNext1 = new Booking();
        bookingNext1.setStart(ZonedDateTime.now().plusYears(20));
        bookingNext1.setEnd(ZonedDateTime.now().plusYears(21));
        bookingNext1.setId(21);

        Booking bookingNext2 = new Booking();
        bookingNext2.setStart(ZonedDateTime.now().plusYears(1));
        bookingNext2.setEnd(ZonedDateTime.now().plusYears(2));
        bookingNext2.setId(22);

        InfoBookingByItemDto infoNext = new InfoBookingByItemDto();
        infoNext.setId(bookingNext2.getId());
        itemWithDateBookingResponse.setNextBooking(null);
        itemWithDateBookingResponse.setLastBooking(null);

        item.getOwner().setId(userId);
        when(itemRepository.getItemById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemWithDateBookingResponse(item))
                .thenReturn(itemWithDateBookingResponse);
        when(bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(itemId, StatusBooking.APPROVED))
                .thenReturn(List.of(bookingLast, bookingNext1, bookingNext2));

        when(itemMapper.toInfoBookingByItemDto(bookingLast)).thenReturn(infoLast);
        when(itemMapper.toInfoBookingByItemDto(bookingNext2)).thenReturn(infoNext);

        ItemWithDateBookingResponse itemActual = itemService.readItem(userId, itemId);
        assertEquals(bookingLast.getId(), itemActual.getLastBooking().getId());
        assertEquals(bookingNext2.getId(), itemActual.getNextBooking().getId());
        verify(itemRepository).getItemById(itemId);
    }

    @Test
    void readItem_whenNotItemInDB_thenThrows() {
        when(itemRepository.getItemById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.readItem(userId, itemId));
        verify(itemRepository).getItemById(itemId);
    }

    @Test
    void getItems_whenRightResponse_thenItemWithActualBookingInfo() {
        item.setId(itemId);
        Booking bookingLast = new Booking();
        bookingLast.setEnd(ZonedDateTime.now().minusYears(20));
        bookingLast.setStart(ZonedDateTime.now().minusYears(21));
        bookingLast.setId(20);
        bookingLast.setItem(item);

        InfoBookingByItemDto infoLast = new InfoBookingByItemDto();
        infoLast.setId(bookingLast.getId());

        List<Item> list = new ArrayList<>(List.of(item));
        Page<Item> pageItem = new PageImpl<>(list, PageRequest.of(0, 2), 1);
        Page<Booking> pageBooking = new PageImpl<>(new ArrayList<>(List.of(bookingLast)), PageRequest.of(0, 2), 1);
        itemWithDateBookingResponse.setId(itemId);

        when(itemRepository.findAllByOwnerIdOrderById(anyInt(), any(Pageable.class))).thenReturn(pageItem);
        when(bookingRepository.getBookingsByOwnerItemAndStatus(userId, StatusBooking.APPROVED, null))
                .thenReturn(pageBooking);
        when(itemMapper.toItemWithDateBookingResponse(item))
                .thenReturn(itemWithDateBookingResponse);
        when(itemMapper.toInfoBookingByItemDto(bookingLast)).thenReturn(infoLast);

        List<ItemWithDateBookingResponse> itemsActual = itemService.getItems(userId, 0, 2);

        assertEquals(bookingLast.getId(), itemsActual.get(0).getLastBooking().getId());
        assertEquals(item.getId(), itemsActual.get(0).getId());
        verify(itemRepository).findAllByOwnerIdOrderById(anyInt(), any(Pageable.class));
    }

    @Test
    void changeItem_whenPatchDesc_thenVerifyMethodWithPatchItemDto() {
        PatchItemRequestDto patch = random.nextObject(PatchItemRequestDto.class);
        patch.setDescription("patchDescription");

        item.getOwner().setId(userId);
        when(itemRepository.getItemById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);

        itemService.changeItem(userId, itemId, patch);
        assertEquals(patch.getDescription(), itemDto.getDescription());
        verify(itemRepository).save(item);
    }

    @Test
    void changeItem_withBadRequestUserId_thenThrows() {
        PatchItemRequestDto patch = new PatchItemRequestDto();
        item.getOwner().setId(99);
        when(itemRepository.getItemById(itemId)).thenReturn(Optional.of(item));

        assertThrows(UnknownValueException.class, () -> itemService.changeItem(userId, itemId, patch));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void getItemsByTextSearch_withRightTextSearch_thenActualSizeListWithVerifyMethod() {
        String text = "TextSearch";
        Page<Item> pageItem = new PageImpl<>(new ArrayList<>(List.of(item)), PageRequest.of(0, 2), 1);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findItemsByTextSearch(anyString(), any(Pageable.class)))
                .thenReturn(pageItem);
        when(itemMapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);

        List<ItemResponse> listActual = itemService.getItemsByTextSearch(1, text, 0, 2);
        assertEquals(1, listActual.size());
        verify(itemRepository).findItemsByTextSearch(anyString(), any(Pageable.class));
    }

    @Test
    void getItemsByTextSearch_withEmptyText_thenEmptyList() {
        String text = "     ";
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        List<ItemResponse> listActual = itemService.getItemsByTextSearch(1, text, 0, 10);

        assertEquals(0, listActual.size());
        verify(itemRepository, never()).findItemsByTextSearch(anyString(), any(Pageable.class));
    }

    @Test
    void createComment_whenRightRequest_thenVerifyMethod() {
        Booking booking = random.nextObject(Booking.class);

        when(bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(anyInt(), anyInt(),
                any(ZonedDateTime.class), anyString())).thenReturn(Optional.of(booking));
        when(itemMapper.toCommentDto(any(CreateCommentRequestDto.class), any(User.class), any(Item.class)))
                .thenReturn(new CommentDto());
        when(itemMapper.toComment(any(CommentDto.class))).thenReturn(new Comment());
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());
        when(itemMapper.toCommentResponse(any(Comment.class))).thenReturn(new CommentResponse());

        itemService.createComment(userId, itemId, new CreateCommentRequestDto());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_withBadRequestUserId_thenThrows() {
        when(bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(anyInt(), anyInt(),
                any(ZonedDateTime.class), anyString())).thenReturn(Optional.empty());

        assertThrows(CustomValueException.class, () ->
                itemService.createComment(userId, itemId, new CreateCommentRequestDto()));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}