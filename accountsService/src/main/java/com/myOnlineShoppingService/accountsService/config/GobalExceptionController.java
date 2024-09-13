package com.myOnlineShoppingService.accountsService.config;

import com.myOnlineShoppingService.accountsService.exception.AccountNotBelongToOwnerException;
import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.exception.CustomerNotFoundException;
import com.myOnlineShoppingService.accountsService.models.StatusMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GobalExceptionController {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<StatusMessage> handleAccountNotFoundException(AccountNotFoundException ex) {
        StatusMessage statusMessage = new StatusMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(statusMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<StatusMessage> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        StatusMessage statusMessage = new StatusMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(statusMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountNotBelongToOwnerException.class)
    public ResponseEntity<StatusMessage> handleAccountNotBelongToOwnerException(AccountNotBelongToOwnerException ex) {
        StatusMessage statusMessage = new StatusMessage(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return new ResponseEntity<>(statusMessage, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public StatusMessage handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new StatusMessage(HttpStatus.PRECONDITION_FAILED.value(), "El argumento no es valido");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.PRECONDITION_FAILED);
    }
}
