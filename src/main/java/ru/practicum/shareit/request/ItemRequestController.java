package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponse createdRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestBody @Valid CreateItemRequestReqDto createItemRequestReqDto) {
        return itemRequestService.createdRequest(userId, createItemRequestReqDto);
    }

    @GetMapping
    public List<ItemRequestResponse> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getRequestsAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestParam(required = false) Integer from,
                                                    @RequestParam(required = false) Integer size) {
        return itemRequestService.getRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getItemRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @PathVariable int requestId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }

}
