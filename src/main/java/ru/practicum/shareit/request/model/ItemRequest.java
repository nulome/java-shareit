package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@ToString
public class ItemRequest {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDate created;
}