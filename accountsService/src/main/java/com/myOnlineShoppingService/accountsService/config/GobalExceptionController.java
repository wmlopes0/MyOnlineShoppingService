package com.myOnlineShoppingService.accountsService.config;

import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.models.StatusMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GobalExceptionController {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<StatusMessage> handleAccountNotFoundException(AccountNotFoundException ex) {
        StatusMessage statusMessage = new StatusMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(statusMessage, HttpStatus.NOT_FOUND);
    }
}
