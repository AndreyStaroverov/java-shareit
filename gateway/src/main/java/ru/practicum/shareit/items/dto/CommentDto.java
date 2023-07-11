package ru.practicum.shareit.items.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @Positive
    private Long id;
    @NotBlank
    private String text;
    @NotBlank
    private String authorName;
    private LocalDateTime created;
}