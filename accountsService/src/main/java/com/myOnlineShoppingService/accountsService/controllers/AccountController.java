package com.myOnlineShoppingService.accountsService.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @RequestMapping("/account")
    public String account() {
        return "ACCOUNT";
    }
}
