package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.controllers.IAccountController;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountServiceIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testCreateAccount_success() {
        // Given
        String url = "http://localhost:" + port + "/accounts/?ownerId=1";
        AccountDTO accountDTO = new AccountDTO()
                .setType("Personal")
                .setBalance(100)
                .setOwner_id(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<AccountDTO> request = new HttpEntity<>(accountDTO, headers);

        // When
        ResponseEntity<AccountDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, AccountDTO.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getType()).isEqualTo(accountDTO.getType());
        assertThat(response.getBody().getBalance()).isEqualTo(accountDTO.getBalance());
        assertThat(response.getBody().getOwner_id()).isEqualTo(accountDTO.getOwner_id());

        // Verify account is persisted
        String getUrl = "http://localhost:" + port + "/accounts/" + response.getBody().getId() + "?ownerId=" + response.getBody().getOwner_id();
        ResponseEntity<AccountDTO> getResponse = restTemplate.getForEntity(getUrl, AccountDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(response.getBody().getId());
        assertThat(getResponse.getBody().getType()).isEqualTo(accountDTO.getType());
        assertThat(getResponse.getBody().getBalance()).isEqualTo(accountDTO.getBalance());
        assertThat(getResponse.getBody().getOwner_id()).isEqualTo(accountDTO.getOwner_id());
    }


    @Test
    void testDeleteAccount_notFound() {
        // Given
        Long nonExistentId = 999L;
        Long ownerId = 1L; // Utiliza un valor adecuado para el ownerId
        String url = getBaseUrl() + "/accounts/" + nonExistentId + "?ownerId=" + ownerId;

        // When
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
