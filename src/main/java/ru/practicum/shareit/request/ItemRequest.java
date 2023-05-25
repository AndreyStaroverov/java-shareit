package ru.practicum.shareit.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    @NotNull
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private User requestor;
    @NotNull
    private LocalDateTime created;

}
