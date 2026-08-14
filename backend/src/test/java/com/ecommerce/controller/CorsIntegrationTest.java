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
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_cors",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CorsIntegrationTest {

    @LocalServerPort
    private int port;

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("Valid preflight to a public route: 200 + CORS headers + Max-Age")
    void preflightValidPublicRoute() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/products"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://frontend.com")
                .header("Access-Control-Request-Method", "GET")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("*");

        String allowMethods = response.headers().firstValue("Access-Control-Allow-Methods").orElse("");
        assertThat(allowMethods).contains("GET").contains("OPTIONS");

        assertThat(response.headers().firstValue("Access-Control-Max-Age")).hasValue("3600");
    }

    @Test
    @DisplayName("Preflight to a protected route without credentials: 200 (CorsFilter cuts before authorization)")
    void preflightProtectedRouteWithoutAuth() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/orders"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://frontend.com")
                .header("Access-Control-Request-Method", "POST")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("*");
    }

    @Test
    @DisplayName("Real GET with Origin: 200 + Access-Control-Allow-Origin")
    void actualRequestWithOrigin() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/products"))
                .GET()
                .header("Origin", "http://frontend.com")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("*");
    }

    @Test
    @DisplayName("OPTIONS without preflight headers on a protected route: 401 (not treated as preflight)")
    void plainOptionsIsNotPreflight() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/orders"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://frontend.com")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(401);
    }
}
