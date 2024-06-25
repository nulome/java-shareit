package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse createdRequest(int userId, CreateItemRequestReqDto createItemRequestReqDto);

    List<ItemRequestResponse> getRequests(int userId);

    List<ItemRequestResponse> getRequestsAll(int userId, Integer from, Integer size);

    ItemRequestResponse getItemRequest(int requestId, int userId);

}
