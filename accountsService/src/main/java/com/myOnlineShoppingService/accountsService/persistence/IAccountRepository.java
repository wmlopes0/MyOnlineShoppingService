package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.exception.InsufficientFundsException;
import com.myOnlineShoppingService.accountsService.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long> {

    @Query("DELETE FROM Account a WHERE a.owner.id = :id")
    @Modifying
    void deleteAllAccountsFromCustomer(@Param("id") Long id);

    List<Account> findByOwner_Id(Long ownerId);

    default void withdrawFromAccounts(Long ownerId, int amount) {
        List<Account> accounts = findByOwner_Id(ownerId);
        int totalBalance = accounts.stream().mapToInt(Account::getBalance).sum();

        if (totalBalance < amount) {
            throw new InsufficientFundsException("Insufficient funds in all accounts.");
        }

        int remainingAmount = amount;

        for (Account account : accounts) {
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

