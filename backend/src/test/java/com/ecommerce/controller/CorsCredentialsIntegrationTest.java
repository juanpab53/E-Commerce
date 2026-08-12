package com.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_cors_credentials",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "app.cors.allow-credentials=true",
        "app.cors.allowed-origins=*"
})
class CorsCredentialsIntegrationTest {

    @LocalServerPort
    private int port;

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("Modo credenciales (futuro JWT en cookie): ACAO espeja el Origin + Allow-Credentials true")
    void preflightWithCredentials() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/productos"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://frontend.com")
                .header("Access-Control-Request-Method", "GET")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("http://frontend.com");
        assertThat(response.headers().firstValue("Access-Control-Allow-Credentials")).hasValue("true");
    }
}
