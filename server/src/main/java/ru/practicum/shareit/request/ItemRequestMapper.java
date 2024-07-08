package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", imports = ZonedDateTime.class)
public abstract class ItemRequestMapper {

    @Autowired
    UserMapper userMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", expression = "java(userMapper.toUserDto(user))")
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    public abstract ItemRequestReqDto toReqDto(CreateItemRequestReqDto createItemRequestReqDto, User user);

    @Mapping(target = "requestor", expression = "java(toUser(itemRequestReqDto.getRequestor()))")
    public abstract ItemRequest toItemRequest(ItemRequestReqDto itemRequestReqDto);

    @Mapping(target = "created", expression = "java(patchToZone(itemRequestResponse.getCreated()))")
    public abstract ItemRequest toItemRequest(ItemRequestResponse itemRequestResponse);

    @Mapping(target = "requestor", expression = "java(userMapper.toResponse(itemRequest.getRequestor()))")
    @Mapping(target = "created", expression = "java(convertToLocal(itemRequest.getCreated()))")
    public abstract ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest);


    @Mapping(target = "created", expression = "java(convertToLocal(itemRequestShortDto.getCreated()))")
    public abstract ItemRequestResponse toItemRequestResponse(ItemRequestShortDto itemRequestShortDto);

    public abstract ItemRequestShortDto toItemRequestShortDto(ItemRequest itemRequest);

    @Named("toUser")
    User toUser(UserDto userDto) {
        if (userDto != null) {
            return userMapper.toUser(userDto);
        }
        return null;
    }

    @Named("convertToLocal")
    LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }

    @Named("patchToZone")
    ZonedDateTime patchToZone(LocalDateTime ldt) {
        ZonedDateTime checkZone = ZonedDateTime.now();
        ZoneId zoneId = checkZone.getZone();
        ZoneOffset zoneOffset = checkZone.getOffset();
        return ZonedDateTime.ofInstant(ldt, zoneOffset, zoneId);
    }

}
