package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ItemMapper itemMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "start", expression = "java(patchToZone(createBookingRequestDto.getStart()))")
    @Mapping(target = "end", expression = "java(patchToZone(createBookingRequestDto.getEnd()))")
    @Mapping(target = "status", constant = "WAITING")
    public abstract BookingDto toBookingDto(CreateBookingRequestDto createBookingRequestDto, User user, Item item);

    @Mapping(target = "start", expression = "java(convertToLocal(booking.getStart()))")
    @Mapping(target = "end", expression = "java(convertToLocal(booking.getEnd()))")
    @Mapping(target = "booker", expression = "java(userMapper.toResponse(booking.getBooker()))")
    @Mapping(target = "item", expression = "java(itemMapper.toItemResponse(booking.getItem()))")
    public abstract BookingResponse toBookingResponse(Booking booking);

    public abstract Booking toBooking(BookingDto bookingDto);


    @Named("patchToZone")
    ZonedDateTime patchToZone(LocalDateTime ldt) {
        ZonedDateTime checkZone = ZonedDateTime.now();
        ZoneId zoneId = checkZone.getZone();
        ZoneOffset zoneOffset = checkZone.getOffset();
        return ZonedDateTime.ofInstant(ldt, zoneOffset, zoneId);
    }

    @Named("convertToLocal")
    LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }


}
