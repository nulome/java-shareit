package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
}
