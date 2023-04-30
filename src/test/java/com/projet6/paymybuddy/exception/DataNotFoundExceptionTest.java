package com.projet6.paymybuddy.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataNotFoundExceptionTest {

    @Test
    public void testDataNotFoundException() {
        String expectedMessage = "Data is not found";
        DataMissingException exception = new DataMissingException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
