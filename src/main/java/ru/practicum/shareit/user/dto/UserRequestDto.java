package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @NotNull
    @Positive
    private Integer id;
    @NotBlank
    @Size(max = 255)
    private String name;
    @NotNull
    @Email
    @Size(max = 256)
    private String email;
}
