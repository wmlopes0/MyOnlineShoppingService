package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;

public interface IAccountMapper {

    AccountDTO mapToAccountDTO(Account account);

    Account mapToAccount(AccountDTO accountDTO);
}
