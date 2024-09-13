package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.services.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IAccountService accountsService;

    // GET /accounts
    @GetMapping("")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountsService.listAll();
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(accounts);
    }

    // GET /accounts/{accountId}
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable("accountId") Long accountId) {
        Account account = accountsService.getAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        return ResponseEntity.ok(account);
    }

    // POST /accounts
    @PostMapping("")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountsService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    // PUT /accounts/{accountId}
    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable("accountId") Long accountId,
                                                 @RequestBody Account accountDetails) {
        Account updatedAccount = accountsService.updateAccount(accountId, accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }

    // DELETE /accounts/{accountId}
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("accountId") Long accountId) {
        accountsService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /accounts/user/{ownerId}
    @DeleteMapping("/user/{ownerId}")
    public ResponseEntity<Void> deleteAccountsByOwner(@PathVariable("ownerId") Long ownerId) {
        accountsService.deleteAccountsByOwner(ownerId);
        return ResponseEntity.noContent().build();
    }

    // PUT /accounts/withdraw/{accountId}?amount={amount}&ownerId={ownerId}
    @PutMapping("/withdraw/{accountId}")
    public ResponseEntity<Account> withdrawFromAccount(@PathVariable("accountId") Long accountId,
                                                       @RequestParam("amount") int amount,
                                                       @RequestParam("ownerId") Long ownerId) {
        Account updatedAccount = accountsService.withdrawMoney(accountId, amount, ownerId);
        return ResponseEntity.ok(updatedAccount);
    }

}