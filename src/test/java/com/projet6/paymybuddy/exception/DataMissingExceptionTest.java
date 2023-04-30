package com.projet6.paymybuddy.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataMissingExceptionTest {

    @Test
    public void testDataMissingException() {
        String expectedMessage = "Data is missing";
        DataMissingException exception = new DataMissingException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}

