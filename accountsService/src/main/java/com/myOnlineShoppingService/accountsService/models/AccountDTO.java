package com.myOnlineShoppingService.accountsService.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDTO {

    private Long id;

    @Pattern(regexp = "Personal|Company", message = "Type no valido")
    private String type;

    @Min(value = 0, message = "El balance debe ser mayor o igual a 0")
    private int balance;

    @NotNull(message = "Owner id not null")
    private Long owner_id;
}
