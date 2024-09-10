package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.owner.id = :id")
    List<Account> getAccountsFromCustomer(@Param("id") Long id);

    @Query("DELETE FROM Account a WHERE a.owner.id = :id")
    @Modifying
    void deleteAllAccountsFromCustomer(@Param("id") Long id);
}
