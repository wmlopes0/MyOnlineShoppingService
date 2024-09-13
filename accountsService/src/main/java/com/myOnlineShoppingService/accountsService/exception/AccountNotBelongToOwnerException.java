package com.myOnlineShoppingService.accountsService.exception;

public class AccountNotBelongToOwnerException extends RuntimeException{
    private static final long serialVersionUID = 2L;

    public AccountNotBelongToOwnerException(String message) {
        super(message);
    }
}
