package com.scholastic.litplatformbaapi.controller.advice;

import com.scholastic.litplatformbaapi.exception.AssessmentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(AssessmentException.class)
    public ResponseEntity<String> handleException(AssessmentException e) {
        LOGGER.error(e.getExceptionMessage());
        return ResponseEntity.status(e.getStatus()).body(e.getExceptionMessage());
    }
}
