package org.example.internship_order_service.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.internship_order_service.dto.item.OrderItemRequestDTO;
import org.example.internship_order_service.dto.order.OrderRequestDTO;
import org.example.internship_order_service.dto.order.OrderResponseDTO;
import org.example.internship_order_service.entity.Item;
import org.example.internship_order_service.repository.ItemRepository;
import org.example.internship_order_service.repository.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderIntegrationTest {

    private static final String JWT_SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0cy0xMjM0NTY3ODkw";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static WireMockServer wireMockServer = new WireMockServer(0);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("user-service.base-url", () -> "http://localhost:" + wireMockServer.port());
        registry.add("jwt.secret", () -> JWT_SECRET);
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Long itemId;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        Item item = new Item();
        item.setName("Laptop");
        item.setPrice(BigDecimal.valueOf(1200));
        itemId = itemRepository.save(item).getId();

        wireMockServer.stubFor(get(urlEqualTo("/api/internal/users/10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 10,
                                "name": "Ivan",
                                "surname": "Ivanov",
                                "email": "ivan@mail.com",
                                "birthdate": "1990-01-01",
                                "active": true
                            }
                            """)));
    }

    @Test
    void createOrder_shouldPersistOrderAndEnrichWithUser() {
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setItemId(itemId);
        itemRequest.setQuantity(2);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(10L);
        request.setOrderItems(List.of(itemRequest));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(generateAdminToken(10L));
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<OrderResponseDTO> response = restTemplate.postForEntity(
                baseUrl() + "/api/orders", httpEntity, OrderResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(2400));
        assertThat(response.getBody().getUser()).isNotNull();
        assertThat(response.getBody().getUser().getName()).isEqualTo("Ivan");
    }

    @Test
    void createOrder_shouldReturnNullUser_whenUserServiceUnavailable() {
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlMatching("/api/internal/users/.*"))
                .willReturn(aResponse().withStatus(500)));

        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setItemId(itemId);
        itemRequest.setQuantity(1);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(10L);
        request.setOrderItems(List.of(itemRequest));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(generateAdminToken(10L));
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<OrderResponseDTO> response = restTemplate.postForEntity(
                baseUrl() + "/api/orders", httpEntity, OrderResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser()).isNull();
    }

    private String generateAdminToken(Long authUserId) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(JWT_SECRET));
        return Jwts.builder()
                .subject(String.valueOf(authUserId))
                .claim("role", "ROLE_ADMIN")
                .claim("token_type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key)
                .compact();
    }
}


