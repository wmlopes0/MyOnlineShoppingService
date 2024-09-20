package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.controllers.abstrac.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:data.sql")
public class RoleIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @CsvSource({
            "cajero@cajero.com, cajero",
            "director@director.com, director"
    })
    public void testCajeroAccessToAccounts(String email, String role) throws Exception {
        HttpHeaders headers = createHeaders(email, role);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/user/1")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
