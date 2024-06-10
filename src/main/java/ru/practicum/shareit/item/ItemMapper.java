package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", imports = ZonedDateTime.class)
public abstract class ItemMapper {


    public abstract ItemWithDateBookingResponse toItemWithDateBookingResponse(Item item);


    @Mapping(target = "owner", source = "user")
    @Mapping(target = "name", expression = "java(createItemRequestDto.getName())")
    public abstract ItemDto toItemDto(CreateItemRequestDto createItemRequestDto, User user);

    public abstract Item toItem(ItemDto itemDto);

    public abstract ItemDto toItemDto(Item item);

    public abstract ItemDto toItemDto(ItemRequestDto itemRequestDto);

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

    @Named("convertToLocal")
    LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }

}
