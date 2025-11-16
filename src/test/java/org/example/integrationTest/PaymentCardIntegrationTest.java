package org.example.integrationTest;

import org.example.dto.PaymentCarddto;
import org.example.dto.Userdto;
import org.example.model.repository.UsersRepository;
import org.example.model.repository.PaymentCardsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentCardIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName
            .parse("postgres:15.3"))
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("pass");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PaymentCardsRepository cardsRepository;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
        usersRepository.deleteAll();
        cardsRepository.deleteAll();
    }

    @Test
    void fullPaymentCardFlowIntegrationTest() {


        Userdto userDTO = new Userdto();
        userDTO.setName("Alex");
        userDTO.setSurname("Maxov");
        userDTO.setBirthDate(new Date());
        userDTO.setEmail("Alexmaxov@gmail.com");
        userDTO.setActive(true);

        ResponseEntity<Userdto> userResponse = restTemplate.postForEntity(
                baseUrl + "/users", userDTO, Userdto.class
        );

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer userId = userResponse.getBody().getId();


        PaymentCarddto cardDTO = new PaymentCarddto();
        cardDTO.setUserId(userId);
        cardDTO.setCardNumber("2222555588885555");
        cardDTO.setHolder("Alex Maxov");
        cardDTO.setExpirationDate(LocalDateTime.now().plusYears(3));
        cardDTO.setActive(true);

        ResponseEntity<PaymentCarddto> cardResponse = restTemplate.postForEntity(
                baseUrl + "/cards", cardDTO, PaymentCarddto.class
        );

        assertThat(cardResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PaymentCarddto createdCard = cardResponse.getBody();
        assertThat(createdCard.getUserId()).isEqualTo(userId);


        ResponseEntity<PaymentCarddto[]> cardsByUserResponse = restTemplate.getForEntity(
                baseUrl + "/cards/user/" + userId, PaymentCarddto[].class
        );

        assertThat(cardsByUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<PaymentCarddto> cards = List.of(cardsByUserResponse.getBody());
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getCardNumber()).isEqualTo("2222555588885555");



        PaymentCarddto updateDTO = new PaymentCarddto();
        updateDTO.setCardNumber("5555666677778888");
        updateDTO.setHolder("Alex Updated");
        updateDTO.setExpirationDate(LocalDateTime.now().plusYears(5));
        updateDTO.setActive(true);
        updateDTO.setUserId(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentCarddto> updateRequest = new HttpEntity<>(updateDTO, headers);

        ResponseEntity<PaymentCarddto> updatedResponse = restTemplate.exchange(
                baseUrl + "/cards/" + createdCard.getId(),
                HttpMethod.PUT,
                updateRequest,
                PaymentCarddto.class
        );

        assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedResponse.getBody().getCardNumber()).isEqualTo("5555666677778888");


        ResponseEntity<Void> deactivateResponse = restTemplate.exchange(
                baseUrl + "/cards/" + createdCard.getId() + "/deactivate",
                HttpMethod.PATCH,
                null,
                Void.class
        );

        assertThat(deactivateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<PaymentCarddto> getDeactivatedResponse = restTemplate.getForEntity(
                baseUrl + "/cards/" + createdCard.getId(),
                PaymentCarddto.class
        );
        assertThat(getDeactivatedResponse.getBody().getActive()).isFalse();
    }
}