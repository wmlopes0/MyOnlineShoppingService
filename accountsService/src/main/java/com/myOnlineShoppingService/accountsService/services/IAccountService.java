package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    List<Account> listAll();

    Optional<Account> getAccountById(Long id);

    List<Account> listAllFromCustomer(Long ownerId);

    Account createAccount(Account newAccount);

    Account updateAccount(Account updateAccount);

    boolean deleteAccount(Long id);

    void addMoney(Long accountId, int amount, Customer owner);

    Account withdrawMoney(Long accountId, int amount, Long ownerId);

    boolean deleteAccountsByOwner(Long id);
}
