package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.exception.AccountNotBelongToOwnerException;
import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.exception.CustomerNotFoundException;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IAccountRepository repoAccount;
    @Autowired
    private ICustomerRepository repoCustomer;

    @Override
    public List<Account> listAll() {
        return repoAccount.findAll();
    }

    @Override
    public Account findAccountByIdAndOwnerId(Long accountId, Long ownerId) {
        Account account = repoAccount.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID: " + accountId + " not found"));

        if (!account.getOwner().getId().equals(ownerId)) {
            throw new AccountNotBelongToOwnerException("Account with ID: " + accountId + " does not belong to owner with ID: " + ownerId);
        }
        return account;
    }

    @Override
    public List<Account> listByOwnerId(Long ownerId) {
        return repoAccount.findByOwner_Id(ownerId);
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return repoAccount.findById(id);
    }

    @Override
    public List<Account> listAllFromCustomer(Long ownerId) {
        return repoAccount.findByOwner_Id(ownerId);
    }

    @Override
    public Account createAccount(Account newAccount) {
        return repoAccount.save(newAccount);
    }

    @Override
    @Transactional
    public Account updateAccount(Account updateAccount) {
        Optional<Account> accountDB = repoAccount.findById(updateAccount.getId());
        if (!accountDB.isEmpty()) {
            accountDB.get().setType(updateAccount.getType());
            accountDB.get().setBalance(updateAccount.getBalance());
        } else {
            throw new RuntimeException("La cuenta no existe");
        }
        return repoAccount.save(updateAccount);
    }

    @Override
    @Transactional
    public boolean deleteAccount(Long id) {
        Optional<Account> accountDB = repoAccount.findById(id);
        if (!accountDB.isEmpty()) {
            repoAccount.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Account addMoney(Long accountId, int amount, Long ownerId) {
        Account accountDB = findAccountByIdAndOwnerId(accountId, ownerId);
        accountDB.setBalance(accountDB.getBalance() + amount);
        return repoAccount.save(accountDB);
    }

    @Override
    public Account withdrawMoney(Long accountId, int amount, Long ownerId) {
        Account accountDB = findAccountByIdAndOwnerId(accountId, ownerId);
        accountDB.setBalance(accountDB.getBalance() - amount);
        return repoAccount.save(accountDB);
    }

    @Override
    public boolean deleteAccountsByOwner(Long id) {
        List<Account> accounts = repoAccount.findByOwner_Id(id);
        if (!accounts.isEmpty()) {
            repoAccount.deleteAllAccountsFromCustomer(id);
            return true;
        } else {
            throw new AccountNotFoundException("Accounts not exists.");
        }
    }

    @Override
    public boolean isLoanPossible(Long ownerId, Double loanAmount) {
        List<Account> accounts = repoAccount.findByOwner_Id(ownerId);

        double totalBalance = accounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();

        double maxLoanAmount = totalBalance * 0.8;

        return loanAmount <= maxLoanAmount;
    }

    @Override
    public void verifyOwnerExists(Long ownerId) {
        Optional<Customer> customer = repoCustomer.findById(ownerId);
        if (customer.isEmpty()) throw new CustomerNotFoundException("Customer not exists.");
    }
}
