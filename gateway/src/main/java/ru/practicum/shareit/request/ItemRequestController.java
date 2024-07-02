package ru.practicum.shareit.request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;

import static ru.practicum.shareit.related.Constants.CONTROLLER_REQUEST_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@Controller
@RequestMapping(path = CONTROLLER_REQUEST_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createdRequest(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                 @RequestBody @Valid CreateItemRequestReqDto createItemRequestReqDto) {
        log.info("Creating request {}, userId={}", createItemRequestReqDto, userId);
        return itemRequestClient.createdRequest(userId, createItemRequestReqDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId) {
        log.info("Get request by userId={}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                 @PathVariable int requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }

}
