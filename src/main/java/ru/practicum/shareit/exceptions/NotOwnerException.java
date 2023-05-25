package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotOwnerException extends RuntimeException{
    public NotOwnerException(String message) {
        super(message);
    }
}
