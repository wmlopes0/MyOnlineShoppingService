package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import com.myOnlineShoppingService.accountsService.services.IAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
                .setId(9L)
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
                .setOwner(customer1)
                .setType("Personal")
                .setBalance(1500);
        Account account4 = new Account()
                .setId(4L)
                .setOwner(customer1)
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
    public void getAccountsByClient() {
        Long ownerId = 9L;
        List<AccountDTO> expectedAccounts = Arrays.asList(
                new AccountDTO(3L, "Personal", 1500, 9L),
                new AccountDTO(4L, "Personal", 1000, 9L)
        );

        ResponseEntity<List<AccountDTO>> response = iAccountController.getAccountsByOwnerId(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAccounts, response.getBody());

    }
}
