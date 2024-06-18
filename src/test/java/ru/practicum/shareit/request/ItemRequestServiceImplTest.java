package ru.practicum.shareit.request;

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
import ru.practicum.shareit.handler.CustomValueException;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;

    private final EasyRandom random = new EasyRandom();

    private CreateItemRequestReqDto createItemRequestReqDto;
    private ItemRequestReqDto itemRequestReqDto;
    private ItemRequest itemRequest;
    private ItemRequestResponse itemRequestResponse;
    private User user;
    int userId = 1;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRequestMapper);
        createItemRequestReqDto = random.nextObject(CreateItemRequestReqDto.class);
        itemRequestReqDto = random.nextObject(ItemRequestReqDto.class);
        itemRequest = random.nextObject(ItemRequest.class);
        itemRequestResponse = random.nextObject(ItemRequestResponse.class);
        user = random.nextObject(User.class);
    }

    @Test
    void createdRequest_whenRightRequest_thenVerifyMethod() {
        CreateItemRequestReqDto request = createItemRequestReqDto;
        ItemRequestResponse response = itemRequestResponse;
        response.setId(1);
        response.getRequestor().setId(userId);


        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toReqDto(any(CreateItemRequestReqDto.class), any(User.class)))
                .thenReturn(itemRequestReqDto);
        when(itemRequestMapper.toItemRequest(any(ItemRequestReqDto.class)))
                .thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestResponse(itemRequest)).thenReturn(response);

        itemRequestService.createdRequest(userId, request);


        assertEquals(userId, response.getRequestor().getId());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createdRequest_whenNotUser_thenThrows() {
        CreateItemRequestReqDto request = createItemRequestReqDto;
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.createdRequest(userId, request));
    }

    @Test
    void getRequests_whenRightRequest_thenListSize() {
        List<ItemRequest> list = List.of(itemRequest);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(list);
        when(itemRequestMapper.toItemRequestResponse(any(ItemRequest.class))).thenReturn(itemRequestResponse);

        assertEquals(1, itemRequestService.getRequests(userId).size());
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    @Test
    void getRequestsAll_whenRightRequest_thenListSize() {
        List<ItemRequest> list = new ArrayList<>(List.of(itemRequest, itemRequest));
        Page<ItemRequest> page = new PageImpl<>(list, PageRequest.of(0, 2), 10L);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyInt(), any(Pageable.class)))
                .thenReturn(page);
        when(itemRequestMapper.toItemRequestResponse(any(ItemRequest.class))).thenReturn(itemRequestResponse);

        assertEquals(2, itemRequestService.getRequestsAll(userId, 0, 2).size());
        verify(itemRequestRepository).findAllByRequestorIdNotOrderByCreatedDesc(anyInt(), any(Pageable.class));
    }

    @Test
    void getRequestsAll_whenBadRequestParamPageable_thenThrows() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        assertThrows(CustomValueException.class, () -> itemRequestService.getRequestsAll(userId, 0, 0));
        verify(itemRequestRepository, never()).findAllByRequestorIdNotOrderByCreatedDesc(anyInt(), any(Pageable.class));

        assertThrows(CustomValueException.class, () -> itemRequestService.getRequestsAll(userId, -1, 2));
        verify(itemRequestRepository, never()).findAllByRequestorIdNotOrderByCreatedDesc(anyInt(), any(Pageable.class));
    }

    @Test
    void getItemRequest_whenRightRequest_thenItem() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestResponse(any(ItemRequest.class))).thenReturn(itemRequestResponse);

        assertEquals(itemRequestResponse.getId(), itemRequestService.getItemRequest(1, userId).getId());
        verify(itemRequestRepository).getItemRequestById(anyInt());
    }

    @Test
    void getItemRequest_whenBadRequestId_thenThrows() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getItemRequestById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(-1, userId));
        verify(itemRequestRepository).getItemRequestById(anyInt());
    }
}