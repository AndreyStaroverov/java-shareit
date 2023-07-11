package ru.practicum.shareit.items.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Positive
    private Long id;
    @NotBlank
    private String text;
    private Item item;
    private User user;
    private LocalDateTime dateCreated;
}