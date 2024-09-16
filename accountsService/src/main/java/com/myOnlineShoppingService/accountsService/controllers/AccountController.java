package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.StatusMessage;
import com.myOnlineShoppingService.accountsService.services.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountController implements IAccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IAccountService accountsService;


    // GET /accounts/{accountId}?ownerId={ownerId}
    @Override
    public ResponseEntity<AccountDTO> getAccountByIdAndOwnerId(@PathVariable Long accountId, @RequestParam Long ownerId) {
        AccountDTO account = accountsService.findAccountByIdAndOwnerId(accountId, ownerId);
        return ResponseEntity.ok(account);
    }


    // GET /accounts/user/{ownerId}
    @Override
    public ResponseEntity<List<AccountDTO>> getAccountsByOwnerId(@PathVariable Long ownerId) {
        List<AccountDTO> accounts = accountsService.listByOwnerId(ownerId);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for owner with ID: " + ownerId);
        }
        return ResponseEntity.ok(accounts);
    }

    // POST /accounts?ownerId={ownerId}
    @Override
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO account, @RequestParam Long ownerId) {
        accountsService.verifyOwnerExists(ownerId);

        account.setOwner_id(ownerId);

        AccountDTO createdAccount = accountsService.createAccount(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    // PUT /accounts/{accountId}?ownerId={ownerId}
    @Override
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountDTO updatedAccountData,
            @RequestParam Long ownerId) {

        AccountDTO existingAccount = accountsService.findAccountByIdAndOwnerId(accountId, ownerId);

        existingAccount.setType(updatedAccountData.getType());
        existingAccount.setBalance(updatedAccountData.getBalance());

        AccountDTO updatedAccount = accountsService.updateAccount(existingAccount);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedAccount);
    }

    // DELETE /accounts/{accountId}?ownerId={ownerId}
    @Override
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId,
            @RequestParam Long ownerId) {

        AccountDTO existingAccount = accountsService.findAccountByIdAndOwnerId(accountId, ownerId);

        accountsService.deleteAccount(existingAccount.getId());

        return ResponseEntity.noContent().build();
    }

    //    PUT /accounts/{accountId}/deposit?amount={amount}&ownerId={ownerId}
    @Override
    public ResponseEntity<AccountDTO> addFromAccount(@PathVariable("accountId") Long accountId,
                                                     @RequestParam("amount") int amount,
                                                     @RequestParam("ownerId") Long ownerId) {
        AccountDTO updatedAccount = accountsService.addMoney(accountId, amount, ownerId);
        return ResponseEntity.ok(updatedAccount);
    }


    //    PUT /accounts/{accountId}/withdraw?amount={amount}&ownerId={ownerId}
    @Override
    public ResponseEntity<AccountDTO> withdrawFromAccount(@PathVariable("accountId") Long accountId,
                                                          @RequestParam("amount") int amount,
                                                          @RequestParam("ownerId") Long ownerId) {
        AccountDTO updatedAccount = accountsService.withdrawMoney(accountId, amount, ownerId);
        return ResponseEntity.ok(updatedAccount);
    }


    //    DELETE /accounts/user/{ownerId}
    @Override
    public ResponseEntity<Void> deleteAccountsByOwner(@PathVariable("ownerId") Long ownerId) {
        accountsService.deleteAccountsByOwner(ownerId);
        return ResponseEntity.noContent().build();
    }

    // GET /accounts/user/{ownerId}/loan?amount={loanAmount}
    @Override
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