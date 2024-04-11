package com.solovev.configuration;

import com.solovev.exception.DataNotFoundException;
import com.solovev.exception.MyConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerControllerAdvice {
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handle(DataNotFoundException e) {
        log.info("Not found data {}",e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(MyConstraintViolationException.class)
    public ResponseEntity<?> handle(MyConstraintViolationException e) {
        log.info("Attempt to violate constraint {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}
