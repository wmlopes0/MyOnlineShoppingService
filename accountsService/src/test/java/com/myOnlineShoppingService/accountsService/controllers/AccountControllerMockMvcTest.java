package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import com.myOnlineShoppingService.accountsService.util.JsonUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerMockMvcTest {
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


        mockMvc.perform(post("/accounts/")
                        .param("ownerId", "2")
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
        AccountDTO invalidAccount = new AccountDTO()
                .setId(5L)
                .setType("Fail")
                .setBalance(5500)
                .setOwner_id(2L);

        String invalidAccountJson = JsonUtil.mapToJson(invalidAccount);


        mockMvc.perform(post("/accounts/")
                        .param("ownerId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAccountJson))
                .andExpect(status().isPreconditionFailed());
    }


    @Test
    @DisplayName("Prueba positiva de updateAccount")
    void updateAccount_PositiveTest() throws Exception {
        AccountDTO updatedAccount = new AccountDTO()
                .setOwner_id(2L)
                .setType("Company")
                .setBalance(2000);

        String updatedAccountJson = JsonUtil.mapToJson(updatedAccount);


        mockMvc.perform(put("/accounts/3")
                        .param("ownerId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAccountJson))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.type", is("Company")))
                .andExpect(jsonPath("$.balance", is(2000)))
                .andExpect(jsonPath("$.owner_id", is(2)));
    }

    @Test
    @DisplayName("Prueba negativa de updateAccount")
    void updateAccount_NegativeTest() throws Exception {
        AccountDTO updatedAccount = new AccountDTO()
                .setOwner_id(2L)
                .setType("Personal")
                .setBalance(2000);

        String updatedAccountJson = JsonUtil.mapToJson(updatedAccount);


        mockMvc.perform(put("/accounts/9")
                        .param("ownerId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAccountJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Prueba positiva de deleteAccount")
    void deleteAccount_PositiveTest() throws Exception {
        mockMvc.perform(delete("/accounts/2")
                        .param("ownerId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Prueba negativa de deleteAccount")
    void deleteAccount_NegativeTest() throws Exception {
        mockMvc.perform(delete("/accounts/15")
                        .param("ownerId", "2"))
                .andExpect(status().isNotFound());
    }
}