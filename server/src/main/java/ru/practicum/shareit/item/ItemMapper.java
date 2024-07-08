package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CommentShortDto;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", imports = ZonedDateTime.class)
public abstract class ItemMapper {

    @Autowired
    ItemRequestMapper itemRequestMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(createItemRequestDto.getName())")
    @Mapping(target = "description", expression = "java(createItemRequestDto.getDescription())")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "request", expression = "java(toItemRequestResponse(itemRequest))")
    public abstract ItemDto toItemDto(CreateItemRequestDto createItemRequestDto, User user, ItemRequest itemRequest);

    public abstract ItemWithDateBookingResponse toItemWithDateBookingResponse(Item item);

    @Mapping(target = "request", expression = "java(itemRequestMapper.toItemRequest(itemDto.getRequest()))")
    public abstract Item toItem(ItemDto itemDto);

    @Mapping(target = "request", expression = "java(toItemRequestResponse(item.getRequest()))")
    public abstract ItemDto toItemDto(Item item);

    public abstract ItemDto toItemDto(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestId", expression = "java(getRequestId(item.getRequest()))")
    public abstract ItemResponse toItemResponse(Item item);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    @Mapping(target = "start", expression = "java(convertToLocal(booking.getStart()))")
    @Mapping(target = "end", expression = "java(convertToLocal(booking.getEnd()))")
    public abstract InfoBookingByItemDto toInfoBookingByItemDto(Booking booking);

    @Mapping(target = "author", source = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    public abstract CommentDto toCommentDto(CreateCommentRequestDto createCommentRequestDto, User user, Item item);

    public abstract Comment toComment(CommentDto commentDto);

    @Mapping(target = "created", expression = "java(convertToLocal(comment.getCreated()))")
    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    public abstract CommentResponse toCommentResponse(Comment comment);

    @Mapping(target = "created", expression = "java(convertToLocal(commentShortDto.getCreated()))")
    public abstract CommentResponse toCommentResponse(CommentShortDto commentShortDto);

    @Named("convertToLocal")
    LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }

    @Named("getRequestId")
    Integer getRequestId(ItemRequest itemRequest) {
        if (itemRequest != null) {
            return itemRequest.getId();
        }
        return null;
    }

    @Named("getRequestId")
    ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

}
