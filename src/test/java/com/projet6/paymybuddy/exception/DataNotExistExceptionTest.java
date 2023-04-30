package com.projet6.paymybuddy.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataNotExistExceptionTest {

    @Test
    public void testDataNotExistException() {
        String expectedMessage = "Data is not existing";
        DataNotExistException exception = new DataNotExistException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
