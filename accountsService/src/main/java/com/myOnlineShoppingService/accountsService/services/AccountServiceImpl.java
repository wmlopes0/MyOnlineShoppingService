package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IAccountRepository repo;

    @Override
    public List<Account> listAll() {
        return repo.findAll();
    }

    @Override
    public Account listOneAccount(Long id) {
        return repo.findById(id).get();
    }

    @Override
    public List<Account> listAllFromCustomer(Long ownerId) {
        return repo.getAccountFromCustomer(ownerId);
    }

    @Override
    public Account createAccount(Account newAccount) {
        return repo.save(newAccount);
    }

    @Override
    @Transactional
    public Account updateAccount(Account updateAccount) {
        Optional<Account> accountDB = repo.findById(updateAccount.getId());
        if (!accountDB.isEmpty()) {
            accountDB.get().setType(updateAccount.getType());
            accountDB.get().setBalance(updateAccount.getBalance());
        } else {
            throw new RuntimeException("La cuenta no existe");
        }
        return repo.save(updateAccount);
    }

    @Override
    @Transactional
    public boolean deleteAccount(Long id) {
        Optional<Account> accountDB = repo.findById(id);
        if (!accountDB.isEmpty()) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void addMoney(Long accountId, int amount, Customer owner) {
        Optional<Account> accountDB = repo.findById(accountId);
        if (!accountDB.isEmpty()) {
            accountDB.get().setBalance(accountDB.get().getBalance() + amount);
        } else {
            throw new RuntimeException("La cuenta no existe");
        }

        repo.save(accountDB.get());
    }

    @Override
    public void withdrawMoney(Long accountId, int amount, Customer owner) {
        Optional<Account> accountDB = repo.findById(accountId);
        if (!accountDB.isEmpty()) {
            accountDB.get().setBalance(accountDB.get().getBalance() - amount);
        } else {
            throw new RuntimeException("La cuenta no existe");
        }

        repo.save(accountDB.get());
    }

    @Override
    public boolean deleteAllAccountsFromCustomer(Long id) {
        List<Account> accounts = repo.getAccountFromCustomer(id);
        if (!accounts.isEmpty()) {
            repo.deleteAllAccountsFromCustomer(id);
            return true;
        } else {
            return false;
        }
    }
}
