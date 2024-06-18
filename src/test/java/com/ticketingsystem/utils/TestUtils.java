package com.ticketingsystem.utils;

import com.ticketingsystem.dtos.jwt.JwtRequest;
import com.ticketingsystem.dtos.jwt.JwtResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class TestUtils {

    public static String getJwtToken(TestRestTemplate restTemplate, String port) {
        JwtRequest request = new JwtRequest();
        request.setEmail("BARA");
        request.setPassword("1234");

        JwtResponse response = restTemplate.postForObject("http://localhost:" + port + "/auth/login", request, JwtResponse.class);
        assert response != null;
        return response.getJwtToken();
    }

    public static ResponseEntity<String> sendRequest(TestRestTemplate restTemplate, String url, HttpMethod httpMethod, Object object, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Object> entity = new HttpEntity<>(object, headers);
        return restTemplate.exchange(url, httpMethod, entity, String.class);
    }

}
