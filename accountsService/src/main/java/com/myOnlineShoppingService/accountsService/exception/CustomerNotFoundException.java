package com.myOnlineShoppingService.accountsService.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String s) {
        super(s);
    }
}
