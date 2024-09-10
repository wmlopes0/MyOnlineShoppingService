package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class IAccountRepositoryTest {

    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private ICustomerRepository customerRepository;

    @Test
    @Transactional
    void getAccountsFromCustomer() {
        Customer owner = new Customer()
                .setId(1L);
        customerRepository.save(owner);

        Account account1 = new Account()
                .setOwner(owner)
                .setType("TYPE")
                .setBalance(1000);
        accountRepository.save(account1);

        Account account2 = new Account()
                .setOwner(owner)
                .setType("TYPE")
                .setBalance(2000);
        accountRepository.save(account2);

        List<Account> accounts = accountRepository.getAccountsFromCustomer(owner.getId());

        Assertions.assertEquals(2, accounts.size());
        Assertions.assertTrue(accounts.containsAll(Arrays.asList(account1, account2)));
    }


}