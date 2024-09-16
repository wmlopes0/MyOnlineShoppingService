package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.exception.InsufficientFundsException;
import com.myOnlineShoppingService.accountsService.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
        // Obtén todas las cuentas del usuario
        List<Account> accounts = findByOwner_Id(ownerId);

        // Busca la cuenta específica
        Account primaryAccount = accounts.stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        // Verifica si el total del saldo de todas las cuentas es suficiente
        int totalBalance = accounts.stream().mapToInt(Account::getBalance).sum();
        if (totalBalance < amount) {
            throw new InsufficientFundsException("Insufficient funds in all accounts.");
        }

        // Primero, intenta retirar de la cuenta específica
        int remainingAmount = amount;
        int primaryBalance = primaryAccount.getBalance();

        if (primaryBalance >= remainingAmount) {
            // Si la cuenta específica tiene suficiente saldo, retira el monto completo
            primaryAccount.setBalance(primaryBalance - remainingAmount);
        } else {
            // Si no tiene suficiente, retira lo que pueda y ajusta el remainingAmount
            remainingAmount -= primaryBalance;
            primaryAccount.setBalance(0);

            // Ahora intenta retirar de las demás cuentas
            for (Account account : accounts) {
                // Saltar la cuenta principal ya que se procesó
                if (account.getId().equals(accountId)) {
                    continue;
                }

                int accountBalance = account.getBalance();
                if (accountBalance >= remainingAmount) {
                    // Si una cuenta tiene suficiente saldo, retira el restante
                    account.setBalance(accountBalance - remainingAmount);
                    break;
                } else {
                    // Si no tiene suficiente, retira lo que pueda y sigue con la siguiente
                    remainingAmount -= accountBalance;
                    account.setBalance(0);
                }
            }
        }
    }

}

