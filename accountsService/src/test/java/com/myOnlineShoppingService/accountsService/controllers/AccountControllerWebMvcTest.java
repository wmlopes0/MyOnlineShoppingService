package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import com.myOnlineShoppingService.accountsService.persistence.ICustomerRepository;
import com.myOnlineShoppingService.accountsService.services.IAccountMapper;
import com.myOnlineShoppingService.accountsService.services.IAccountService;
import com.myOnlineShoppingService.accountsService.util.JsonUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
public class AccountControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    IAccountRepository accountRepository;

    @MockBean
    ICustomerRepository customerRepository;
    @MockBean
    private IAccountService accountsService;
    @MockBean
    private IAccountMapper mapper;

    //Data
    private Customer customer1;
    private Customer customer2;
    private Account account1;
    private Account account2;
    private Account account3;
    private Account account4;
    private AccountDTO accountDTO1;
    private AccountDTO accountDTO2;
    private AccountDTO accountDTO3;
    private AccountDTO accountDTO4;

    @BeforeEach
    void init() {
        customer1 = new Customer()
                .setId(1L)
                .setName("Customer1")
                .setEmail("Email1");
        customer2 = new Customer()
                .setId(2L)
                .setName("Customer2")
                .setEmail("Email2");
        account1 = new Account()
                .setId(1L)
                .setOwner(customer1)
                .setType("Personal")
                .setBalance(1500);
        account2 = new Account()
                .setId(2L)
                .setOwner(customer1)
                .setType("Company")
                .setBalance(1500);
        account3 = new Account()
                .setId(3L)
                .setOwner(customer2)
                .setType("Personal")
                .setBalance(1500);
        account4 = new Account()
                .setId(4L)
                .setOwner(customer2)
                .setType("Personal")
                .setBalance(1000);
        accountDTO1 = new AccountDTO()
                .setId(account1.getId())
                .setOwner_id(account1.getOwner().getId())
                .setType(account1.getType())
                .setBalance(account1.getBalance());

        accountDTO2 = new AccountDTO()
                .setId(account2.getId())
                .setOwner_id(account2.getOwner().getId())
                .setType(account2.getType())
                .setBalance(account2.getBalance());

        accountDTO3 = new AccountDTO()
                .setId(account3.getId())
                .setOwner_id(account3.getOwner().getId())
                .setType(account3.getType())
                .setBalance(account3.getBalance());

        accountDTO4 = new AccountDTO()
                .setId(account4.getId())
                .setOwner_id(account4.getOwner().getId())
                .setType(account4.getType())
                .setBalance(account4.getBalance());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer2));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(account3));
        when(accountRepository.findById(4L)).thenReturn(Optional.of(account4));
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        when(mapper.mapToAccountDTO(account1)).thenReturn(accountDTO1);
        when(mapper.mapToAccountDTO(account2)).thenReturn(accountDTO2);
        when(mapper.mapToAccountDTO(account3)).thenReturn(accountDTO3);
        when(mapper.mapToAccountDTO(account4)).thenReturn(accountDTO4);

        when(mapper.mapToAccount(accountDTO1)).thenReturn(account1);
        when(mapper.mapToAccount(accountDTO2)).thenReturn(account2);
        when(mapper.mapToAccount(accountDTO3)).thenReturn(account3);
        when(mapper.mapToAccount(accountDTO4)).thenReturn(account4);

    }

    @Test
    @DisplayName("Prueba positiva de getAccountByIdAndOwnerId")
    void getAccountByIdAndOwnerId_PositiveTest() throws Exception {
        when(accountsService.findAccountByIdAndOwnerId(4L, 2L)).thenReturn(accountDTO4);

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
