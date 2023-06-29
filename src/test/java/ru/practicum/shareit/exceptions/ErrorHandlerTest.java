package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    ErrorHandler errorHandler;

    @Test
    void handleBadDataException() {
        assertEquals(errorHandler
                .handleBadDataException(new InvalidDataException("Message"))
                .getError(), new ErrorResponse("Message").getError());
    }
}