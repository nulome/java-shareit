package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
public class BookingMapper {

    private static final ModelMapper modelMapper = new ModelMapper();


    public static BookingDto toBookingDto(CreateBookingRequestDto createBookingRequestDto) {
        return BookingDto.builder()
                .start(patchToZone(createBookingRequestDto.getStart()))
                .end(patchToZone(createBookingRequestDto.getEnd()))
                .status(StatusBooking.WAITING)
                .build();
    }

    public static BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(convertToLocal(booking.getStart()))
                .end(convertToLocal(booking.getEnd()))
                .item(modelMapper.map(booking.getItem(), ItemResponse.class))
                .booker(modelMapper.map(booking.getBooker(), UserResponse.class))
                .status(booking.getStatus())
                .build();
    }

    private static ZonedDateTime patchToZone(LocalDateTime ldt) {
        ZonedDateTime checkZone = ZonedDateTime.now();
        ZoneId zoneId = checkZone.getZone();
        ZoneOffset zoneOffset = checkZone.getOffset();
        return ZonedDateTime.ofInstant(ldt, zoneOffset, zoneId);
    }

    private static LocalDateTime convertToLocal(ZonedDateTime zdt) {
        return zdt.toLocalDateTime();
    }
}
