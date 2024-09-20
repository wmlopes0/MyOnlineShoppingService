package com.myOnlineShoppingService.accountsService.controllers.abstrac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myOnlineShoppingService.accountsService.models.AuthRequest;
import com.myOnlineShoppingService.accountsService.models.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected HttpHeaders createHeaders(String user,String pass) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        // Crea un AuthRequest con las credenciales de un usuario válido (por ejemplo, un Cajero o Director)
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user); // Reemplaza con un email válido
        authRequest.setPassword(pass); // Reemplaza con la contraseña correcta

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + authRequest.getEmail() + "\", \"password\": \"" + authRequest.getPassword() + "\"}"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Extrae el token del cuerpo de la respuesta
        String responseContent = result.getResponse().getContentAsString();
        AuthResponse authResponse = new ObjectMapper().readValue(responseContent, AuthResponse.class);
        headers.setBearerAuth(authResponse.getAccessToken());

        return headers;
    }

}
