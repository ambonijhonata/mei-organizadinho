package com.meiorganizadinho.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorMessage> handleBusinessException(BusinessException ex){
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleBusinessException(MethodArgumentNotValidException ex){
        List<FieldError> fieldsWithError = ex.getBindingResult().getFieldErrors();
        String detailsErrorMessage = null;
        if(!fieldsWithError.isEmpty()) {
            detailsErrorMessage = fieldsWithError.getFirst().getDefaultMessage();
        }


        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                detailsErrorMessage
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String detailsErrorMessage = "Invalid request body";

        if (ex.getMessage() != null) {
            String message = ex.getMessage().toLowerCase();

            if (message.contains("unexpected end-of-input") ||
                    message.contains("expected value") || message.contains("expected a value")) {
                detailsErrorMessage = "Name is required";
            } else if (message.contains("cannot deserialize value")) {
                detailsErrorMessage = "Invalid value format";
            }
        }

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                detailsErrorMessage
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleClientNotFoundException(ClientNotFoundException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

}
