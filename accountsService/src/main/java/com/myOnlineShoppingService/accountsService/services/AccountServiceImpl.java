package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.exception.AccountNotBelongToOwnerException;
import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.exception.CustomerNotFoundException;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IAccountRepository repoAccount;
    @Autowired
    private ICustomerRepository repoCustomer;

    @Autowired
    private IAccountMapper accountMapper;

    @Override
    public List<AccountDTO> listAll() {
        return repoAccount.findAll().stream()
                .map(accountMapper::mapToAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO findAccountByIdAndOwnerId(Long accountId, Long ownerId) {
        Account account = repoAccount.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID: " + accountId + " not found"));

        if (!account.getOwner().getId().equals(ownerId)) {
            throw new AccountNotBelongToOwnerException("Account with ID: " + accountId + " does not belong to owner with ID: " + ownerId);
        }
        return accountMapper.mapToAccountDTO(account);
    }

    @Override
    public List<AccountDTO> listByOwnerId(Long ownerId) {
        return repoAccount.findByOwner_Id(ownerId).stream()
                .map(accountMapper::mapToAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AccountDTO> getAccountById(Long id) {
        Optional<Account> optAccount = repoAccount.findById(id);
        if (optAccount.isPresent()) {
            AccountDTO accountDTO = accountMapper.mapToAccountDTO(optAccount.get());
            return Optional.of(accountDTO);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<AccountDTO> listAllFromCustomer(Long ownerId) {
        return repoAccount.findByOwner_Id(ownerId).stream()
                .map(accountMapper::mapToAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO createAccount(AccountDTO newAccount) {
        return accountMapper.mapToAccountDTO(
                repoAccount.save(accountMapper.mapToAccount(newAccount)));
    }

    @Override
    @Transactional
    public AccountDTO updateAccount(AccountDTO updateAccount) {
        Optional<Account> accountDB = repoAccount.findById(updateAccount.getId());
        if (accountDB.isPresent()) {
            Account accountToUpdate = accountDB.get();
            accountToUpdate.setType(updateAccount.getType());
            accountToUpdate.setBalance(updateAccount.getBalance());

            Account updatedAccount = repoAccount.save(accountToUpdate);

            return accountMapper.mapToAccountDTO(updatedAccount);
        } else {
            throw new AccountNotFoundException("Account not exits.");
        }
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
    public AccountDTO addMoney(Long accountId, int amount, Long ownerId) {
        AccountDTO accountDB = findAccountByIdAndOwnerId(accountId, ownerId);
        accountDB.setBalance(accountDB.getBalance() + amount);
        return accountMapper.mapToAccountDTO(repoAccount.save(accountMapper.mapToAccount(accountDB)));
    }

    @Override
    public AccountDTO withdrawMoney(Long accountId, int amount, Long ownerId) {
        AccountDTO accountDB = findAccountByIdAndOwnerId(accountId, ownerId);
        repoAccount.withdrawFromAccounts(ownerId, amount);
        return accountMapper.mapToAccountDTO(repoAccount.save(accountMapper.mapToAccount(accountDB)));
    }


    @Override
    @Transactional
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
