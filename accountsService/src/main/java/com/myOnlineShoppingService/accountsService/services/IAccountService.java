package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;

import java.util.List;

public interface IAccountService {
    List<Account> listAll();

    Account listOneAccount(Long id);

    List<Account> listAllFromCustomer(Long ownerId);

    Account createAccount(Account newAccount);

    Account updateAccount(Account updateAccount);

    boolean deleteAccount(Long id);

    void addMoney(Long accountId, int amount, Customer owner);

    void withdrawMoney(Long accountId, int amount, Customer owner);

    boolean deleteAllAccountsFromCustomer(Long id);
}
