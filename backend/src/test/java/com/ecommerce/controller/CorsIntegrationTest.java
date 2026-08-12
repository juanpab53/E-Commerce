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
        "spring.datasource.url=jdbc:h2:mem:testdb_cors",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
class CorsIntegrationTest {

    @LocalServerPort
    private int port;

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("Preflight valido a ruta publica: 200 + headers CORS + Max-Age")
    void preflightValidPublicRoute() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/productos"))
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
    @DisplayName("Preflight a ruta protegida sin credenciales: 200 (CorsFilter corta antes de la autorizacion)")
    void preflightProtectedRouteWithoutAuth() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/pedidos"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://frontend.com")
                .header("Access-Control-Request-Method", "POST")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("*");
    }

    @Test
    @DisplayName("GET real con Origin: 200 + Access-Control-Allow-Origin")
    void actualRequestWithOrigin() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/productos"))
                .GET()
                .header("Origin", "http://frontend.com")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("*");
    }

    @Test
    @DisplayName("OPTIONS sin headers de preflight a ruta protegida: 401 (no se trata como preflight)")
    void plainOptionsIsNotPreflight() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/pedidos"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://frontend.com")
                .build();

        HttpResponse<String> response = send(request);

        assertThat(response.statusCode()).isEqualTo(401);
    }
}
