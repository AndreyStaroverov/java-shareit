package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Data
@Builder
public class UserDtoPatch {

    @Positive
    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
