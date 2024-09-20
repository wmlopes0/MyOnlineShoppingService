package com.myOnlineShoppingService.accountsService.controllers;


import com.myOnlineShoppingService.accountsService.AccountsServiceApplication;
import com.myOnlineShoppingService.accountsService.controllers.abstrac.AbstractIntegrationTest;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AccountsServiceApplication.class, properties = {"spring.profiles.active = test"})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerE2ETestIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private ICustomerRepository customerRepository;
    private final String email = "director@director.com";
    private final String pass = "director";

    private HttpHeaders headers;


    @BeforeAll
    void first() throws Exception {
        headers = createHeaders(email, pass);
    }

    @BeforeAll
    void init() {
        Customer customer1 = new Customer()
                .setId(null)
                .setName("Customer1")
                .setEmail("Email1");
        Customer customer2 = new Customer()
                .setId(null)
                .setName("Customer2")
                .setEmail("Email2");
        Account account1 = new Account()
                .setId(null)
                .setOwner(customer1)
                .setType("Personal")
                .setBalance(1500);
        Account account2 = new Account()
                .setId(null)
                .setOwner(customer1)
                .setType("Company")
                .setBalance(1500);
        Account account3 = new Account()
                .setId(null)
                .setOwner(customer2)
                .setType("Personal")
                .setBalance(1500);
        Account account4 = new Account()
                .setId(null)
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



    @Test
    @DisplayName("Test para obtener una cuenta por ID y OwnerID")
    void getAccountByIdAndOwnerId() throws Exception {
        Long accountId = 1L;
        Long ownerId = 1L;

        mockMvc.perform(get("/accounts/{accountId}", accountId)
                        .param("ownerId", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers)
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
                        .headers(headers)

                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

}