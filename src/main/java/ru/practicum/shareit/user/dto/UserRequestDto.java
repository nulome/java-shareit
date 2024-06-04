package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserRequestDto {
    @NotNull
    @Positive
    private Integer id;
    @NotBlank
    @Size(max = 255)
    private String name;
    @NotNull
    @Email
    @Size(max = 155)
    private String email;
}
