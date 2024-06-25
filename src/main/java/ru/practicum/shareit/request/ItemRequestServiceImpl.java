package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.related.Constants.CONTROLLER_REQUEST_PATH;
import static ru.practicum.shareit.related.UtilityClasses.createPageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;


    @Override
    public ItemRequestResponse createdRequest(int userId, CreateItemRequestReqDto createItemRequestReqDto) {
        log.info("Получен запрос Post {} - requestor: {}", CONTROLLER_REQUEST_PATH, userId);
        User user = checkGetUserInDataBase(userId);
        ItemRequestReqDto itemRequestReqDto = itemRequestMapper.toReqDto(createItemRequestReqDto, user);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestReqDto);
        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

    @Override
    public List<ItemRequestResponse> getRequests(int userId) {
        log.info("Получен запрос GET {} - user: {}", CONTROLLER_REQUEST_PATH, userId);
        checkGetUserInDataBase(userId);

        List<ItemRequestShortDto> itemRequestShortList =
                mapperItemRequestToShort(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));
        enrichItemRequestByListItemFromDB(itemRequestShortList);

        return itemRequestShortList.stream()
                .map(itemRequestMapper::toItemRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getRequestsAll(int userId, Integer from, Integer size) {
        log.info("Получен запрос GET {}/all?from={}&size={}", CONTROLLER_REQUEST_PATH, from, size);
        checkGetUserInDataBase(userId);
        Pageable pageable = createPageRequest(from, size);

        List<ItemRequestShortDto> itemRequestShortList = mapperItemRequestToShort(
                itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable).getContent());
        enrichItemRequestByListItemFromDB(itemRequestShortList);

        return itemRequestShortList.stream()
                .map(itemRequestMapper::toItemRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse getItemRequest(int requestId, int userId) {
        log.info("Получен запрос GET {}/{}", CONTROLLER_REQUEST_PATH, requestId);
        checkGetUserInDataBase(userId);
        ItemRequest itemRequest = checkItemRequestInDataBase(requestId);

        List<ItemRequestShortDto> itemRequestShortList = mapperItemRequestToShort(List.of(itemRequest));
        enrichItemRequestByListItemFromDB(itemRequestShortList);

        return itemRequestMapper.toItemRequestResponse(itemRequestShortList.get(0));
    }

    private void enrichItemRequestByListItemFromDB(List<ItemRequestShortDto> itemRequestShortList) {
        List<Integer> listRequestId = itemRequestShortList.stream()
                .map(ItemRequestShortDto::getId)
                .collect(toList());

        Map<Integer, List<ItemShortDto>> itemsByRequestIdMap = itemRepository.findAllByRequestIdInList(listRequestId).stream()
                .collect(groupingBy(ItemShortDto::getRequestId, toList()));

        for (ItemRequestShortDto itemRequestShortDto : itemRequestShortList) {
            itemRequestShortDto.setItems(
                    itemsByRequestIdMap.getOrDefault(itemRequestShortDto.getId(), new ArrayList<>()));
        }
    }

    private List<ItemRequestShortDto> mapperItemRequestToShort(List<ItemRequest> list) {
        return list.stream()
                .map(itemRequestMapper::toItemRequestShortDto)
                .collect(Collectors.toList());
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
