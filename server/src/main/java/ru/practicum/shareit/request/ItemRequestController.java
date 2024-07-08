package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_REQUEST_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = CONTROLLER_REQUEST_PATH)
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponse createdRequest(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                              @RequestBody CreateItemRequestReqDto createItemRequestReqDto) {
        log.info("Получен запрос Post {} - requestor: {}", CONTROLLER_REQUEST_PATH, userId);
        return itemRequestService.createdRequest(userId, createItemRequestReqDto);
    }

    @GetMapping
    public List<ItemRequestResponse> getRequests(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId) {
        log.info("Получен запрос GET {} - user: {}", CONTROLLER_REQUEST_PATH, userId);
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getRequestsAll(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        log.info("Получен запрос GET {}/all?from={}&size={}", CONTROLLER_REQUEST_PATH, from, size);
        return itemRequestService.getRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getItemRequest(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                              @PathVariable int requestId) {
        log.info("Получен запрос GET {}/{}", CONTROLLER_REQUEST_PATH, requestId);
        return itemRequestService.getItemRequest(requestId, userId);
    }

}
