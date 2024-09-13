package com.myOnlineShoppingService.accountsService.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatusMessage {
    private Integer status;
    private String message;

}