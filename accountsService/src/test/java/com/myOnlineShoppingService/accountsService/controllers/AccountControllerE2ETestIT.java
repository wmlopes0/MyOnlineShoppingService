package com.myOnlineShoppingService.accountsService.controllers;


import com.myOnlineShoppingService.accountsService.AccountsServiceApplication;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AccountsServiceApplication.class, properties = {"spring.profiles.active = test"})
@AutoConfigureMockMvc
class AccountControllerE2ETestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private ICustomerRepository customerRepository;

    @BeforeEach
    void init() {
        Customer customer1 = new Customer()
                .setId(1L)
                .setName("Customer1")
                .setEmail("Email1");
        Customer customer2 = new Customer()
                .setId(2L)
                .setName("Customer2")
                .setEmail("Email2");
        Account account1 = new Account()
                .setId(1L)
                .setOwner(customer1)
                .setType("Personal")
                .setBalance(1500);
        Account account2 = new Account()
                .setId(2L)
                .setOwner(customer1)
                .setType("Company")
                .setBalance(1500);
        Account account3 = new Account()
                .setId(3L)
                .setOwner(customer2)
                .setType("Personal")
                .setBalance(1500);
        Account account4 = new Account()
                .setId(4L)
                .setOwner(customer2)
                .setType("Personal")
                .setBalance(1000);

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);
        accountRepository.save(account4);
    }

    @AfterEach
    void finish() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Test para obtener una cuenta por ID y OwnerID")
    void getAccountByIdAndOwnerId() throws Exception {
        Long accountId = 1L;
        Long ownerId = 1L;

        mockMvc.perform(get("/accounts/{accountId}", accountId)
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.owner_id").value(ownerId))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    @DisplayName("Test para obtener una cuenta inexistente por ID y OwnerID")
    void getNonExistentAccountByIdAndOwnerId() throws Exception {
        Long accountId = 999L;
        Long ownerId = 1L;

        mockMvc.perform(get("/accounts/{accountId}", accountId)
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }


    @Test
    void getAccountsByOwnerId() {
    }

    @Test
    void createAccount() {
    }

    @Test
    void updateAccount() {
    }

    @Test
    void deleteAccount() {
    }

    @Test
    void addFromAccount() {
    }

    @Test
    void withdrawFromAccount() {
    }

    @Test
    void deleteAccountsByOwner() {
    }

    @Test
    void checkLoan() {
    }
}