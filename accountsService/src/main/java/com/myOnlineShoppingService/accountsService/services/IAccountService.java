package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    List<Account> listAll();

    Account findAccountByIdAndOwnerId(Long accountId, Long ownerId);

    List<Account> listByOwnerId(Long ownerId);

    Optional<Account> getAccountById(Long id);

    List<Account> listAllFromCustomer(Long ownerId);

    Account createAccount(Account newAccount);

    Account updateAccount(Account updateAccount);

    boolean deleteAccount(Long id);

    Account addMoney(Long accountId, int amount, Long ownerId);

    Account withdrawMoney(Long accountId, int amount, Long ownerId);

    boolean deleteAccountsByOwner(Long id);

    boolean isLoanPossible(Long ownerId, Double loanAmount);

    void verifyOwnerExists(Long ownerId);
}
