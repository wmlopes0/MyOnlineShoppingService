package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
@Sql(scripts = "classpath:customer.sql")
public class AccountControlerRestTest {
    @LocalServerPort
    private int port;

    private String baseUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void init() {
        baseUrl = "http://localhost:" + port + "/accounts";

    }

    @Test
    @Order(0)
    public void testCreateAccount() {
        String url = baseUrl + "/?ownerId=1";
        log.info(url);
        AccountDTO newAccount = new AccountDTO(null, "Personal", 100, 1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountDTO> request = new HttpEntity<>(newAccount, headers);

        ResponseEntity<AccountDTO> response = restTemplate.postForEntity(url, request, AccountDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Personal", response.getBody().getType());
        assertEquals(100, response.getBody().getBalance());
        assertEquals(1, response.getBody().getOwner_id());
    }

    @Test
    @Order(1)
    public void testGetAccount() {
        String url = baseUrl + "/1?ownerId=1";
        ResponseEntity<AccountDTO> response = restTemplate.getForEntity(url, AccountDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals(1, response.getBody().getOwner_id());
    }

    @Test
    @Order(2)
    public void testUpdateAccount() {
        String url = baseUrl + "/1?ownerId=1";
        AccountDTO updatedAccount = new AccountDTO(1L, "Company", 500, 2L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountDTO> request = new HttpEntity<>(updatedAccount, headers);

        ResponseEntity<AccountDTO> response = restTemplate.exchange(url, HttpMethod.PUT, request, AccountDTO.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("Company", response.getBody().getType());
        assertEquals(500, response.getBody().getBalance());
        assertEquals(1, response.getBody().getOwner_id());
    }

    @Test
    @Order(3)
    public void testDeleteAccount() {
        String url = baseUrl + "/1?ownerId=1";
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
