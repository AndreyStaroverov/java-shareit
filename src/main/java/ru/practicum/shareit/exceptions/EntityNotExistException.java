package ru.practicum.shareit.exceptions;

public class EntityNotExistException extends RuntimeException {
    public EntityNotExistException(String message) {
        super(message);
    }
}