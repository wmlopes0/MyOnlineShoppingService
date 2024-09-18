package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import com.myOnlineShoppingService.accountsService.util.JsonUtil;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    ICustomerRepository customerRepository;

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
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Prueba positiva de getAccountByIdAndOwnerId")
    void getAccountByIdAndOwnerId_PositiveTest() throws Exception {
        mockMvc.perform(get("/accounts/4")
                        .param("ownerId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.owner_id", is(2)));
    }

    @Test
    @DisplayName("Prueba negativa de getAccountByIdAndOwnerId")
    void getAccountByIdAndOwnerId_NegativeTest() throws Exception {
        mockMvc.perform(get("/accounts/4")
                        .param("ownerId", "3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Prueba positiva de createAccount")
    void createAccount_PositiveTest() throws Exception {
        AccountDTO account = new AccountDTO()
                .setId(5L)
                .setType("Personal")
                .setBalance(1000)
                .setOwner_id(2L);

        String accountJson = JsonUtil.mapToJson(account);


        mockMvc.perform(post("/accounts")
                        .param("ownerId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson).accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.type", is("Personal")))
                .andExpect(jsonPath("$.balance", is(1000)))
                .andExpect(jsonPath("$.owner_id", is(2)));
    }

    @Test
    @DisplayName("Prueba negativa de createAccount")
    void createAccount_NegativeTest() throws Exception {
        Customer customer5 = new Customer()
                .setId(5L)
                .setName("Customer5")
                .setEmail("Email5");
        customerRepository.save(customer5);

        Account invalidAccount = new Account()
                .setBalance(-500);

        String invalidAccountJson = JsonUtil.mapToJson(invalidAccount);


        mockMvc.perform(post("/accounts")
                        .param("ownerId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAccountJson))
                .andExpect(status().isPreconditionFailed());
    }


    @Test
    @DisplayName("Prueba positiva de updateAccount")
    void updateAccount_PositiveTest() throws Exception {
        Customer customer6 = new Customer()
                .setId(6L)
                .setName("Customer6")
                .setEmail("Email6");
        customerRepository.save(customer6);

        Account account9 = new Account()
                .setId(9L)
                .setOwner(customer6)
                .setType("Personal")
                .setBalance(500);
        accountRepository.save(account9);

        Account updatedAccount = new Account()
                .setType("Business")
                .setBalance(2000);

        String updatedAccountJson = JsonUtil.mapToJson(updatedAccount);


        mockMvc.perform(put("/accounts/9")
                        .param("ownerId", "6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAccountJson))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.type", is("Business")))
                .andExpect(jsonPath("$.balance", is(2000.0)))
                .andExpect(jsonPath("$.owner.id", is(6)));
    }

    @Test
    @DisplayName("Prueba negativa de updateAccount")
    void updateAccount_NegativeTest() throws Exception {
        Customer customer6 = new Customer()
                .setId(6L)
                .setName("Customer6")
                .setEmail("Email6");
        customerRepository.save(customer6);

        Account updatedAccount = new Account()
                .setType("Business")
                .setBalance(2000);

        String updatedAccountJson = JsonUtil.mapToJson(updatedAccount);


        mockMvc.perform(put("/accounts/9")
                        .param("ownerId", "6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAccountJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Prueba positiva de deleteAccount")
    void deleteAccount_PositiveTest() throws Exception {
        Customer customer7 = new Customer()
                .setId(7L)
                .setName("Customer7")
                .setEmail("Email7");
        customerRepository.save(customer7);

        Account account10 = new Account()
                .setId(10L)
                .setOwner(customer7)
                .setType("Personal")
                .setBalance(1000);
        accountRepository.save(account10);


        mockMvc.perform(delete("/accounts/10")
                        .param("ownerId", "7"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Prueba negativa de deleteAccount")
    void deleteAccount_NegativeTest() throws Exception {
        Customer customer7 = new Customer()
                .setId(7L)
                .setName("Customer7")
                .setEmail("Email7");
        customerRepository.save(customer7);


        mockMvc.perform(delete("/accounts/10")
                        .param("ownerId", "7"))
                .andExpect(status().isNotFound());
    }
}