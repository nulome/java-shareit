package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_REQUEST_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = CONTROLLER_REQUEST_PATH)
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponse createdRequest(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                              @RequestBody CreateItemRequestReqDto createItemRequestReqDto) {
        return itemRequestService.createdRequest(userId, createItemRequestReqDto);
    }

    @GetMapping
    public List<ItemRequestResponse> getRequests(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getRequestsAll(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        return itemRequestService.getRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getItemRequest(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                              @PathVariable int requestId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }

}
