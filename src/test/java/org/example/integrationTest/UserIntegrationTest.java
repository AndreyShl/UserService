package org.example.integrationTest;

import org.example.dto.Userdto;
import org.example.model.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@Disabled("Временно")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {
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

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
        usersRepository.deleteAll();
    }

    @Test
    void userFullFlowIntegrationTest() {
        Userdto userDTO = new Userdto();
        userDTO.setName("Alex");
        userDTO.setSurname("Maxov");
        userDTO.setBirthDate(new Date());
        userDTO.setEmail("alex.maxov@example.com");
        userDTO.setActive(true);

        ResponseEntity<Userdto> createResponse = restTemplate.postForEntity(
                baseUrl + "/users", userDTO, Userdto.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Userdto createdUser = createResponse.getBody();
        assertThat(createdUser.getId()).isNotNull();
        Integer userId = createdUser.getId();

        ResponseEntity<Userdto> getResponse = restTemplate.getForEntity(
                baseUrl + "/users/" + userId, Userdto.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getEmail()).isEqualTo("alex.maxov@example.com");

        createdUser.setName("Alexander");
        createdUser.setActive(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Userdto> updateRequest = new HttpEntity<>(createdUser, headers);

        ResponseEntity<Userdto> updateResponse = restTemplate.exchange(
                baseUrl + "/users/" + userId,
                HttpMethod.PUT,
                updateRequest,
                Userdto.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getName()).isEqualTo("Alexander");
        assertThat(updateResponse.getBody().isActive()).isFalse();

        ResponseEntity<Void> activateResponse = restTemplate.exchange(
                baseUrl + "/users/" + userId + "/activate",
                HttpMethod.PATCH,
                null,
                Void.class
        );
        assertThat(activateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверка, что пользователь активен
        ResponseEntity<Userdto> checkActivated = restTemplate.getForEntity(
                baseUrl + "/users/" + userId, Userdto.class
        );
        assertThat(checkActivated.getBody().isActive()).isTrue();


        ResponseEntity<Void> deactivateResponse = restTemplate.exchange(
                baseUrl + "/users/" + userId + "/deactivate",
                HttpMethod.PATCH,
                null,
                Void.class
        );
        assertThat(deactivateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);


        ResponseEntity<Userdto> checkDeactivated = restTemplate.getForEntity(
                baseUrl + "/users/" + userId, Userdto.class
        );
        assertThat(checkDeactivated.getBody().isActive()).isFalse();
    }
}
