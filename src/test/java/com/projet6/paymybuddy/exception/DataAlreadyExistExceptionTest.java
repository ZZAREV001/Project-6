package com.projet6.paymybuddy.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataAlreadyExistExceptionTest {

    @Test
    public void testDataAlreadyExistException() {
        String expectedMessage = "Data already exists";
        DataAlreadyExistException exception = new DataAlreadyExistException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
