package ru.practicum.shareit.items.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoPatch {

    @Positive
    private Long id;
    private String name;
    private String description;
    private Boolean available;

}
