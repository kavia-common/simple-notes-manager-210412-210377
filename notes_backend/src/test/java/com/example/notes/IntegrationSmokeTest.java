package com.example.notes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationSmokeTest {

    @Autowired
    ApplicationContext context;

    @LocalServerPort
    int port;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void openApiAvailable() {
        RestClient client = RestClient.builder().baseUrl("http://localhost:" + port).build();
        String json = client.get().uri("/openapi.json").retrieve().body(String.class);
        assertNotNull(json);
        assertTrue(json.contains("\"openapi\""));
    }
}
