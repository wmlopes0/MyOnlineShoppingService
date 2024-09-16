package com.myOnlineShoppingService.accountsService.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "accounts")
@Schema(name = "Model of Account", description = "Represents an account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private int balance;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private Customer owner;
}
