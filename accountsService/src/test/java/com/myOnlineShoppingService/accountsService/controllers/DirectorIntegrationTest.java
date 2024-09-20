package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.controllers.abstrac.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@SpringBootTest
@AutoConfigureMockMvc
public class DirectorIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testDirectorAccessToAccounts() throws Exception {
        HttpHeaders headers = createHeaders("director@director.com","director");

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/user/1")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
