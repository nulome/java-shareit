package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

@Data
public class ItemRequestReqDto {
    private Integer id;
    private String description;
    private User requestor;
    private ZonedDateTime created;
}
