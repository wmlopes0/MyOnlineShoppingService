package com.myOnlineShoppingService.accountsService.controllers;


import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import com.myOnlineShoppingService.accountsService.services.IAccountService;
import com.myOnlineShoppingService.accountsService.util.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class AccountControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAccountController iAccountController;
    @Autowired
    private IAccountService iAccountService;
    @Autowired
    private ICustomerRepository customerRepository;
    @Autowired
    private IAccountRepository accountRepository;

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
    @DisplayName("Obtener cuentas de cliente")
    public void getAccountsByClient() throws Exception {
        Long ownerId = 1L;

        mockMvc.perform(get("/accounts/user/" + ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].owner_id", is(ownerId.intValue())))
                .andExpect(jsonPath("$[0].type", is("Personal")))
                .andExpect(jsonPath("$[0].balance", is(1500)));
    }

    @Test
    @DisplayName("Crear cuenta para cliente no existente")
    public void createAccountForInvalidCustomer() throws Exception {

        AccountDTO account = new AccountDTO(10L, "Personal", 15000, 999L);
        String accountJSON = JsonUtil.mapToJson(account);


        mockMvc.perform(post("/accounts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJSON))
                .andExpect(status().isBadRequest());
    }
}