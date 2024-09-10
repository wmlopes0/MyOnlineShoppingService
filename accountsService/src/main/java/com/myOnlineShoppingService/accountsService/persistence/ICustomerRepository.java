package com.myOnlineShoppingService.accountsService.persistence;

import com.myOnlineShoppingService.accountsService.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {
}
