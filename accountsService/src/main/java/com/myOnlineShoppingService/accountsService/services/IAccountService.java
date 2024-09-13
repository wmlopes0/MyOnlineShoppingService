package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    List<AccountDTO> listAll();

    AccountDTO findAccountByIdAndOwnerId(Long accountId, Long ownerId);

    List<AccountDTO> listByOwnerId(Long ownerId);

    Optional<AccountDTO> getAccountById(Long id);

    List<AccountDTO> listAllFromCustomer(Long ownerId);

    AccountDTO createAccount(AccountDTO newAccount);

    AccountDTO updateAccount(AccountDTO updateAccount);

    boolean deleteAccount(Long id);

    AccountDTO addMoney(Long accountId, int amount, Long ownerId);

    AccountDTO withdrawMoney(Long accountId, int amount, Long ownerId);

    boolean deleteAccountsByOwner(Long id);

    boolean isLoanPossible(Long ownerId, Double loanAmount);

    void verifyOwnerExists(Long ownerId);
}
