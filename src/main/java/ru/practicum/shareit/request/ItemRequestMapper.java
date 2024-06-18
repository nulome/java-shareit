package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = ZonedDateTime.class)
public abstract class ItemRequestMapper {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ItemMapper itemMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", source = "user")
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    public abstract ItemRequestReqDto toReqDto(CreateItemRequestReqDto createItemRequestReqDto, User user);


    public abstract ItemRequest toItemRequest(ItemRequestReqDto itemRequestReqDto);

    @Mapping(target = "requestor", expression = "java(userMapper.toResponse(itemRequest.getRequestor()))")
    @Mapping(target = "created", expression = "java(convertToLocal(itemRequest.getCreated()))")
    @Mapping(target = "items", expression = "java(toListItemResponse(itemRequest.getListItem()))")
    public abstract ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest);

    @Named("convertToLocal")
    LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }

    @Named("toListItemResponse")
    List<ItemResponse> toListItemResponse(List<Item> list) {
        if (list == null) {
            return null;
        }
        return list.stream()
                .map(itemMapper::toItemResponse)
                .collect(Collectors.toList());
    }

}
