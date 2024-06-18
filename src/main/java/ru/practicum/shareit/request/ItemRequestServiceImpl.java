package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.CustomValueException;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;


    @Override
    public ItemRequestResponse createdRequest(int userId, CreateItemRequestReqDto createItemRequestReqDto) {
        log.info("Получен запрос Post /requests - requestor: {}", userId);
        User user = checkGetUserInDataBase(userId);
        ItemRequestReqDto itemRequestReqDto = itemRequestMapper.toReqDto(createItemRequestReqDto, user);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestReqDto);
        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

    @Override
    public List<ItemRequestResponse> getRequests(int userId) {
        log.info("Получен запрос GET /requests - user: {}", userId);
        checkGetUserInDataBase(userId);

        List<ItemRequest> list = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return list.stream()
                .map(itemRequestMapper::toItemRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getRequestsAll(int userId, Integer from, Integer size) {
        log.info("Получен запрос GET /requests/all?from={}&size={}", from, size);
        checkGetUserInDataBase(userId);
        Pageable pageable = createPageRequest(from, size);
        List<ItemRequest> listReq = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable).getContent();

        return listReq.stream()
                .map(itemRequestMapper::toItemRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse getItemRequest(int requestId, int userId) {
        log.info("Получен запрос GET /requests/{}", requestId);
        checkGetUserInDataBase(userId);
        ItemRequest itemRequest = checkItemRequestInDataBase(requestId);
        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

    private Pageable createPageRequest(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }
        if (size <= 0) {
            throw new CustomValueException("Количество элементов на странице должно быть больше 0");
        }
        if (from < 0) {
            throw new CustomValueException("Не допустимый индекс первого элемента: " + from);
        }
        int number = from / size;
        return PageRequest.of(number, size, Sort.by("created"));
    }

    private User checkGetUserInDataBase(int userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе users не найдено: " + userId));
    }

    private ItemRequest checkItemRequestInDataBase(int requestId) {
        return itemRequestRepository.getItemRequestById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе request не найдено: " + requestId));
    }
}
