package org.example.integrationTest;

import org.example.DTO.UserDTO;
import org.example.DTO.PaymentCardDTO;
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


        UserDTO userDTO = new UserDTO();
        userDTO.setName("Alex");
        userDTO.setSurname("Maxov");
        userDTO.setBirthDate(new Date());
        userDTO.setEmail("Alexmaxov@gmail.com");
        userDTO.setActive(true);

        ResponseEntity<UserDTO> userResponse = restTemplate.postForEntity(
                baseUrl + "/users", userDTO, UserDTO.class
        );

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer userId = userResponse.getBody().getId();


        PaymentCardDTO cardDTO = new PaymentCardDTO();
        cardDTO.setUserId(userId);
        cardDTO.setCardNumber("2222555588885555");
        cardDTO.setHolder("Alex Maxov");
        cardDTO.setExpirationDate(LocalDateTime.now().plusYears(3));
        cardDTO.setActive(true);

        ResponseEntity<PaymentCardDTO> cardResponse = restTemplate.postForEntity(
                baseUrl + "/cards", cardDTO, PaymentCardDTO.class
        );

        assertThat(cardResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PaymentCardDTO createdCard = cardResponse.getBody();
        assertThat(createdCard.getUserId()).isEqualTo(userId);


        ResponseEntity<PaymentCardDTO[]> cardsByUserResponse = restTemplate.getForEntity(
                baseUrl + "/cards/user/" + userId, PaymentCardDTO[].class
        );

        assertThat(cardsByUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<PaymentCardDTO> cards = List.of(cardsByUserResponse.getBody());
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getCardNumber()).isEqualTo("2222555588885555");



        PaymentCardDTO updateDTO = new PaymentCardDTO();
        updateDTO.setCardNumber("5555666677778888");
        updateDTO.setHolder("Alex Updated");
        updateDTO.setExpirationDate(LocalDateTime.now().plusYears(5));
        updateDTO.setActive(true);
        updateDTO.setUserId(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentCardDTO> updateRequest = new HttpEntity<>(updateDTO, headers);

        ResponseEntity<PaymentCardDTO> updatedResponse = restTemplate.exchange(
                baseUrl + "/cards/" + createdCard.getId(),
                HttpMethod.PUT,
                updateRequest,
                PaymentCardDTO.class
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


        ResponseEntity<PaymentCardDTO> getDeactivatedResponse = restTemplate.getForEntity(
                baseUrl + "/cards/" + createdCard.getId(),
                PaymentCardDTO.class
        );
        assertThat(getDeactivatedResponse.getBody().getActive()).isFalse();
    }
}