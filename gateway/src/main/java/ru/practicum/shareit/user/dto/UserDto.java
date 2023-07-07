package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Data
@Builder
public class UserDto {

    @Positive
    private Long id;
    @NotNull
    private String name;
    @NotBlank
    @Email
    private String email;

}