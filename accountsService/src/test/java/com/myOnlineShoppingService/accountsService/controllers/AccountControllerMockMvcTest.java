package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.controllers.abstrac.AbstractIntegrationTest;
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
import org.springframework.http.HttpHeaders;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerMockMvcTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    ICustomerRepository customerRepository;

    @BeforeAll
    void init() {
        Customer customer1 = new Customer()
                .setName("Customer1")
                .setEmail("Email1");
        Customer customer2 = new Customer()
                .setName("Customer2")
                .setEmail("Email2");
        Account account1 = new Account()
                .setOwner(customer1)
                .setType("Personal")
                .setBalance(1500);
        Account account2 = new Account()
                .setOwner(customer1)
                .setType("Company")
                .setBalance(1500);
        Account account3 = new Account()
                .setOwner(customer2)
                .setType("Personal")
                .setBalance(1500);
        Account account4 = new Account()
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
    @DisplayName("Prueba positiva de getAccountByIdAndOwnerId")
    void getAccountByIdAndOwnerId_PositiveTestCajero() throws Exception {
        HttpHeaders headers = createHeaders("cajero@cajero.com", "cajero");
        mockMvc.perform(get("/accounts/4")
                        .param("ownerId", "2")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.owner_id", is(2)));
    }
    @Test
    @DisplayName("Prueba positiva de getAccountByIdAndOwnerId con director")
    void getAccountByIdAndOwnerId_PositiveTestDirector() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com", "director");
        mockMvc.perform(get("/accounts/4")
                        .param("ownerId", "2")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.owner_id", is(2)));
    }

    @Test
    @DisplayName("Prueba negativa de getAccountByIdAndOwnerId con director")
    void getAccountByIdAndOwnerId_NegativeTestDirector() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com", "director");
        mockMvc.perform(get("/accounts/4")
                        .param("ownerId", "3")
                        .headers(headers))
                .andExpect(status().isForbidden());
    }    @Test
    @DisplayName("Prueba negativa de getAccountByIdAndOwnerId con cajero")
    void getAccountByIdAndOwnerId_NegativeTestCajero() throws Exception {
        HttpHeaders headers = createHeaders("cajero@cajero.com", "cajero");

        mockMvc.perform(get("/accounts/4")
                        .param("ownerId", "3")
                        .headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Prueba positiva de createAccount")
    void createAccount_PositiveTest() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com", "director");
        AccountDTO account = new AccountDTO()
                .setType("Personal")
                .setBalance(1000)
                .setOwner_id(2L);

        String accountJson = JsonUtil.mapToJson(account);


        mockMvc.perform(post("/accounts/")
                        .param("ownerId", "2")
                        .headers(headers)
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
        HttpHeaders headers = createHeaders("director@director.com", "director");
        AccountDTO invalidAccount = new AccountDTO()
                .setType("Fail")
                .setBalance(5500)
                .setOwner_id(2L);

        String invalidAccountJson = JsonUtil.mapToJson(invalidAccount);


        mockMvc.perform(post("/accounts/")
                        .param("ownerId", "2")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAccountJson))
                .andExpect(status().isPreconditionFailed());
    }


    @Test
    @DisplayName("Prueba positiva de updateAccount")
    void updateAccount_PositiveTest() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com", "director");
        AccountDTO updatedAccount = new AccountDTO()
                .setType("Company")
                .setBalance(2000);

        String updatedAccountJson = JsonUtil.mapToJson(updatedAccount);


        mockMvc.perform(put("/accounts/2")
                        .param("ownerId", "1")
                        .headers(headers)
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
        HttpHeaders headers = createHeaders("director@director.com", "director");
        AccountDTO updatedAccount = new AccountDTO()
                .setOwner_id(2L)
                .setType("Personal")
                .setBalance(2000);

        String updatedAccountJson = JsonUtil.mapToJson(updatedAccount);


        mockMvc.perform(put("/accounts/9")
                        .headers(headers)
                        .param("ownerId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAccountJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Prueba positiva de deleteAccount")
    void deleteAccount_PositiveTest() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com", "director");
        mockMvc.perform(delete("/accounts/2")
                        .param("ownerId", "1")
                        .headers(headers))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Prueba negativa de deleteAccount")
    void deleteAccount_NegativeTest() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com", "director");
        mockMvc.perform(delete("/accounts/15")
                        .param("ownerId", "2")
                        .headers(headers))
                .andExpect(status().isNotFound());
    }
}