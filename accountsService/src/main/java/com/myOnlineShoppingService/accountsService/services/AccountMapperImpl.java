package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import org.springframework.stereotype.Service;

@Service
public class AccountMapperImpl implements IAccountMapper {
    @Override
    public AccountDTO mapToAccountDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setType(account.getType());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setOwner_id(account.getOwner().getId());
        return accountDTO;
    }

    @Override
    public Account mapToAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setId(accountDTO.getId());
        account.setType(accountDTO.getType());
        account.setBalance(accountDTO.getBalance());
        account.setOwner(new Customer(accountDTO.getOwner_id()));
        return account;
    }
}
