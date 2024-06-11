package com.scholastic.litplatformbaapi.controller.advice;

import com.scholastic.litplatformbaapi.exception.AssessmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerAdviceTest {

    @Test
    @DisplayName("Bad request exception")
    void handleException_badRequest() {
        ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
        var result = advice.handleException(
                new AssessmentException(HttpStatus.BAD_REQUEST, "Bad Request - wrong assessment type"));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Bad Request - wrong assessment type", result.getBody());
    }

    @Test
    @DisplayName("Not found exception")
    void handleException_notFound() {
        ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();
        var result = advice.handleException(
                new AssessmentException(HttpStatus.NOT_FOUND, "No srm assessment found for the student"));

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("No srm assessment found for the student", result.getBody());
    }
}
