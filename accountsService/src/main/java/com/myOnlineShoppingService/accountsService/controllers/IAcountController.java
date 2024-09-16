package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.StatusMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public interface IAcountController {

    @RequestMapping(value = "/accounts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Tag(name = "Account API", description = "API for managing user accounts")
    public interface IAccountController {

        @Operation(summary = "Get an account by ID and owner ID", description = "Returns a single account if found")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = AccountDTO.class))),
                @ApiResponse(responseCode = "404", description = "No account found")
        })
        @GetMapping("/{accountId}")
        ResponseEntity<AccountDTO> getAccountByIdAndOwnerId(@Parameter(description = "ID of the account", required = true) @PathVariable Long accountId,
                                                            @Parameter(description = "Owner ID of the account", required = true) @RequestParam Long ownerId);

        @Operation(summary = "Get all accounts for an owner", description = "Returns all accounts owned by the specified owner")
        @GetMapping("/user/{ownerId}")
        ResponseEntity<List<AccountDTO>> getAccountsByOwnerId(@Parameter(description = "Owner ID to search", required = true) @PathVariable Long ownerId);

        @Operation(summary = "Create an account", description = "Creates a new account")
        @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
        ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO account, @RequestParam Long ownerId);

        @Operation(summary = "Update an account", description = "Updates an existing account")
        @PutMapping(value = "/{accountId}", consumes = MediaType.APPLICATION_JSON_VALUE)
        ResponseEntity<AccountDTO> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountDTO updatedAccountData, @RequestParam Long ownerId);

        @Operation(summary = "Delete an account", description = "Deletes an existing account")
        @DeleteMapping("/{accountId}")
        ResponseEntity<Void> deleteAccount(@PathVariable Long accountId, @RequestParam Long ownerId);

        @Operation(summary = "Deposit money into an account", description = "Deposits an amount into the specified account")
        @PutMapping("/{accountId}/deposit")
        ResponseEntity<AccountDTO> addFromAccount(@PathVariable Long accountId, @RequestParam int amount, @RequestParam Long ownerId);

        @Operation(summary = "Withdraw money from an account", description = "Withdraws an amount from the specified account")
        @PutMapping("/{accountId}/withdraw")
        ResponseEntity<AccountDTO> withdrawFromAccount(@PathVariable Long accountId, @RequestParam int amount, @RequestParam Long ownerId);

        @Operation(summary = "Delete all accounts for an owner", description = "Deletes all accounts owned by the specified owner")
        @DeleteMapping("/user/{ownerId}")
        ResponseEntity<Void> deleteAccountsByOwner(@PathVariable Long ownerId);

        @Operation(summary = "Check loan possibility", description = "Checks if a loan of the specified amount is possible for the owner")
        @GetMapping("/user/{ownerId}/loan")
        ResponseEntity<StatusMessage> checkLoan(@PathVariable Long ownerId, @RequestParam Double loanAmount);
    }

}
