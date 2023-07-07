package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Positive
    private Long id;
    @NotNull
    private String name;
    @Email
    private String email;

}
