package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.exception.InsufficientFundsException;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class IAccountRepositoryTest {
    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    ICustomerRepository customerRepository;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
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

    @AfterEach
    void finish() {
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        entityManager.createNativeQuery("ALTER TABLE accounts ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE customers ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }


    @Test
    @DisplayName("Prueba positiva de findByOwner_Id")
    void findByOwner_Id_PositiveTest() {
        Long customerId = 1L;

        List<Account> accounts = accountRepository.findByOwner_Id(customerId);

        assertThat(accounts, hasSize(2));
        for (Account account : accounts) {
            assertThat(account.getOwner().getId(), equalTo(customerId));
        }
    }

    @Test
    @DisplayName("Prueba negativa de findByOwner_Id")
    void findByOwner_Id_NegativeTest() {
        Long nonExistentCustomerId = 50L;

        List<Account> accounts = accountRepository.findByOwner_Id(nonExistentCustomerId);

        assertThat(accounts, is(empty()));
    }

    @Test
    @DisplayName("Prueba positiva de deleteAllAccountsFromCustomer")
    void deleteAllAccountsFromCustomer_PositiveTest() {
        Long customerId = 2L;
        List<Account> accountsBefore = accountRepository.findByOwner_Id(customerId);
        assertThat(accountsBefore, hasSize(2));

        accountRepository.deleteAllAccountsFromCustomer(customerId);

        List<Account> accountsAfter = accountRepository.findByOwner_Id(customerId);
        assertThat(accountsAfter, is(empty()));
    }

    @Test
    @DisplayName("Prueba negativa de deleteAllAccountsFromCustomer")
    void deleteAllAccountsFromCustomer_NegativeTest() {
        Long nonExistentCustomerId = 50L;
        long totalAccountsBefore = accountRepository.count();

        accountRepository.deleteAllAccountsFromCustomer(nonExistentCustomerId);

        long totalAccountsAfter = accountRepository.count();
        assertThat(totalAccountsAfter, equalTo(totalAccountsBefore));
    }

    @Test
    @DisplayName("Prueba positiva de withdrawFromAccounts")
    void withdrawFromAccounts_PositiveTest() {
        Long accountId = 3L;
        Long customerId = 2L;

        accountRepository.withdrawFromAccounts(accountId, customerId, 500);

        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        assertThat(updatedAccount.getBalance(), equalTo(1000));
    }

    @Test
    @DisplayName("Prueba negativa de withdrawFromAccounts")
    void withdrawFromAccounts_NegativeTest() {
        Long accountId = 3L;
        Long customerId = 2L;

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(InsufficientFundsException.class, () -> {
            accountRepository.withdrawFromAccounts(accountId, customerId, 3500);
        });

        assertThat(exception.getMessage(), containsString("Insufficient funds in all accounts."));
    }
}