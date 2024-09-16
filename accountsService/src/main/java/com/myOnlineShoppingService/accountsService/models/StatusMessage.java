package com.myOnlineShoppingService.accountsService.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(name = "Model of StatusMessage", description = "Represents an message of endpoints")
public class StatusMessage {
    private Integer status;
    private String message;

}