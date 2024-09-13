package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.models.StatusMessage;
import com.myOnlineShoppingService.accountsService.services.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/accounts",
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IAccountService accountsService;

    // GET /accounts/{accountId}?ownerId={ownerId}
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountByIdAndOwnerId(@PathVariable Long accountId, @RequestParam Long ownerId) {
        Account account = accountsService.findAccountByIdAndOwnerId(accountId, ownerId);
        return ResponseEntity.ok(account);
    }


    // GET /accounts/user/{ownerId}
    @GetMapping("/user/{ownerId}")
    public ResponseEntity<List<Account>> getAccountsByOwnerId(@PathVariable Long ownerId) {
        List<Account> accounts = accountsService.listByOwnerId(ownerId);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for owner with ID: " + ownerId);
        }
        return ResponseEntity.ok(accounts);
    }

    // POST /accounts?ownerId={ownerId}
    @PostMapping("/")
    public ResponseEntity<Account> createAccount(@RequestBody Account account, @RequestParam Long ownerId) {
        accountsService.verifyOwnerExists(ownerId);

        Customer owner = new Customer();
        owner.setId(ownerId);
        account.setOwner(owner);

        Account createdAccount = accountsService.createAccount(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    // PUT /accounts/{accountId}?ownerId={ownerId}
    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long accountId,
            @RequestBody Account updatedAccountData,
            @RequestParam Long ownerId) {

        Account existingAccount = accountsService.findAccountByIdAndOwnerId(accountId, ownerId);

        existingAccount.setType(updatedAccountData.getType());
        existingAccount.setBalance(updatedAccountData.getBalance());

        Account updatedAccount = accountsService.updateAccount(existingAccount);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedAccount);
    }

    // DELETE /accounts/{accountId}?ownerId={ownerId}
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId,
            @RequestParam Long ownerId) {

        Account existingAccount = accountsService.findAccountByIdAndOwnerId(accountId, ownerId);

        accountsService.deleteAccount(existingAccount.getId());

        return ResponseEntity.noContent().build();
    }

    //    PUT /accounts/{accountId}/deposit?amount={amount}&ownerId={ownerId}
    @PutMapping("/add/{accountId}/deposit")
    public ResponseEntity<Account> addFromAccount(@PathVariable("accountId") Long accountId,
                                                  @RequestParam("amount") int amount,
                                                  @RequestParam("ownerId") Long ownerId) {
        Account updatedAccount = accountsService.addMoney(accountId, amount, ownerId);
        return ResponseEntity.ok(updatedAccount);
    }


    //    PUT /accounts/{accountId}/withdraw?amount={amount}&ownerId={ownerId}
    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<Account> withdrawFromAccount(@PathVariable("accountId") Long accountId,
                                                       @RequestParam("amount") int amount,
                                                       @RequestParam("ownerId") Long ownerId) {
        Account updatedAccount = accountsService.withdrawMoney(accountId, amount, ownerId);
        return ResponseEntity.ok(updatedAccount);
    }


    //    DELETE /accounts/user/{ownerId}
    @DeleteMapping("/user/{ownerId}")
    public ResponseEntity<Void> deleteAccountsByOwner(@PathVariable("ownerId") Long ownerId) {
        accountsService.deleteAccountsByOwner(ownerId);
        return ResponseEntity.noContent().build();
    }

    // GET /accounts/user/{ownerId}/loan?amount={loanAmount}
    @GetMapping("/accounts/user/{ownerId}/loan")
    public ResponseEntity<StatusMessage> checkLoan(
            @PathVariable Long ownerId,
            @RequestParam Double loanAmount) {

        boolean possible = accountsService.isLoanPossible(ownerId, loanAmount);

        if (possible) {
            return ResponseEntity.ok(new StatusMessage(HttpStatus.OK.value(), "Loan approved."));
        }

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(new StatusMessage(HttpStatus.PRECONDITION_FAILED.value(), "Loan exceeds 80% of total account balance."));
    }


}