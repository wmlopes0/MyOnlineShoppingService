package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.exception.InsufficientFundsException;
import com.myOnlineShoppingService.accountsService.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long> {

    @Query("DELETE FROM Account a WHERE a.owner.id = :id")
    @Modifying
    void deleteAllAccountsFromCustomer(@Param("id") Long id);

    List<Account> findByOwner_Id(Long ownerId);

    @Transactional
    default void withdrawFromAccounts(Long accountId, Long ownerId, int amount) {
        List<Account> accounts = findByOwner_Id(ownerId);

        Account primaryAccount = findById(accountId).get();

        int totalBalance = accounts.stream().mapToInt(Account::getBalance).sum();
        if (totalBalance < amount) {
            throw new InsufficientFundsException("Insufficient funds in all accounts.");
        }

        // Primero, intenta retirar de la cuenta específica
        int remainingAmount = amount;
        int primaryBalance = primaryAccount.getBalance();

        if (primaryBalance >= remainingAmount) {
            primaryAccount.setBalance(primaryBalance - remainingAmount);
            save(primaryAccount);
        } else {
            remainingAmount -= primaryBalance;
            primaryAccount.setBalance(0);
            save(primaryAccount);

            for (Account account : accounts) {
                // Saltar la cuenta principal ya que se procesó
                if (account.getId().equals(accountId)) {
                    continue;
                }

                int accountBalance = account.getBalance();
                if (accountBalance >= remainingAmount) {
                    account.setBalance(accountBalance - remainingAmount);
                    save(account);
                    break;
                } else {
                    remainingAmount -= accountBalance;
                    account.setBalance(0);
                    save(account);
                }
            }
        }
    }

}

