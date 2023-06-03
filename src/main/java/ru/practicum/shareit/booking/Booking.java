package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 *  Это класс заготовка под следующие спринты,
 *  в данном спринте нам не нужно реализовывать booking & request
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @NotNull
    @Positive
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private Long booker;
    @NotNull
    private StatusOfBooking status;
}
