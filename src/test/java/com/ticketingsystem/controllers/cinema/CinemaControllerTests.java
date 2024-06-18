package com.ticketingsystem.controllers.cinema;

import com.ticketingsystem.utils.TestUtils;
import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CinemaControllerTests {

    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void creationRequestTest() {
        String token = TestUtils.getJwtToken(restTemplate, port);

        // Case --> Name is invalid
        Cinema cinema = new Cinema();
        cinema.setName("");
        ResponseEntity<String> response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);
        Assertions.assertEquals("{\"errors\":[\"Cinema Name is mandatory\"]}", response.getBody());

        // Case 2 --> Id is included but name is valid
        cinema = new Cinema();
        cinema.setName("A");
        cinema.setId(1L);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);
        Assertions.assertEquals("{\"errors\":[\"Cinema Id should not be provided in the request body\"]}", response.getBody());

        // Case 3 --> Id is included & name not valid
        cinema = new Cinema();
        cinema.setName("");
        cinema.setId(1L);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Cinema Id should not be provided in the request body") &&
                response.getBody().contains("Cinema Name is mandatory"));

        // Case 4 --> Two different objects are created
        cinema = new Cinema();
        cinema.setName("A");
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);
        Assertions.assertEquals("{\"id\":1,\"name\":\"A\"}", response.getBody());

        cinema = new Cinema();
        cinema.setName("B");
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);
        Assertions.assertEquals("{\"id\":2,\"name\":\"B\"}", response.getBody());
    }

    @Test
    void listCinemaTest() {
        String token = TestUtils.getJwtToken(restTemplate, port);

        Cinema cinema = new Cinema();
        cinema.setName("A");
        TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);

        cinema = new Cinema();
        cinema.setName("B");
        TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);

        String response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.GET, cinema, token).getBody();
        Assertions.assertEquals("[{\"id\":1,\"name\":\"A\"},{\"id\":2,\"name\":\"B\"}]", response);
    }

    @Test
    void listHallsTest() {
        String token = TestUtils.getJwtToken(restTemplate, port);

        // Create Cinema
        Cinema cinema = new Cinema();
        cinema.setName("A");
        TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema", HttpMethod.POST, cinema, token);

        ResponseEntity<String> response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema/2/halls",
                HttpMethod.GET, null, token);
        Assertions.assertEquals("{\"errors\":[\"Cinema not found for the given Id\"]}", response.getBody());

        // No halls
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema/1/halls", HttpMethod.GET, null, token);
        Assertions.assertEquals("[]", response.getBody());

        // Add 2 halls
        cinema.setId(1L);

        Hall hall = new Hall();
        hall.setName("A");
        hall.setNumberOfRows(1);
        hall.setNumberOfColumns(2);
        hall.setCinema(cinema);
        TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);

        hall = new Hall();
        hall.setName("B");
        hall.setNumberOfRows(2);
        hall.setNumberOfColumns(3);
        hall.setCinema(cinema);
        TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);

        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/cinema/1/halls", HttpMethod.GET, null, token);
        Assertions.assertEquals("[{\"id\":1,\"name\":\"A\",\"numberOfRows\":1,\"numberOfColumns\":2,\"cinema\":" +
                "{\"id\":1,\"name\":\"A\"}},{\"id\":2,\"name\":\"B\",\"numberOfRows\":2,\"numberOfColumns\":3,\"cinema\":" +
                "{\"id\":1,\"name\":\"A\"}}]", response.getBody());
    }
}
