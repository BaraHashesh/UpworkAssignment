package com.ticketingsystem.halls;

import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;
import com.ticketingsystem.models.seat.SeatStatus;
import com.ticketingsystem.services.cinema.CinemaService;
import com.ticketingsystem.services.hall.HallService;
import com.ticketingsystem.services.seat.SeatService;
import com.ticketingsystem.utils.TestUtils;
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

import java.util.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class HallTests {

    @LocalServerPort
    private String port;

    @Autowired
    SeatService seatService;

    @Autowired
    HallService hallService;

    @Autowired
    CinemaService cinemaService;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void createHallTest() {
        String token = TestUtils.getJwtToken(restTemplate, port);

        Hall hall = new Hall();
        ResponseEntity<String> response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Cinema is required for hall"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of Rows is required"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of columns is required"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Name is mandatory for the hall"));

        Cinema cinema = new Cinema();
        hall.setCinema(cinema);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Cinema is required for hall"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of Rows is required"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of columns is required"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Name is mandatory for the hall"));

        hall.setName("H");
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Cinema is required for hall"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of Rows is required"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of columns is required"));
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Name is mandatory for the hall"));

        hall.setNumberOfColumns(10);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Cinema is required for hall"));
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Number of Rows is required"));
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Number of columns is required"));
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Name is mandatory for the hall"));

        hall.setNumberOfRows(10);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Cinema is required for hall"));
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Number of Rows is required"));
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Number of columns is required"));
        Assertions.assertFalse(Objects.requireNonNull(response.getBody()).contains("Name is mandatory for the hall"));

        Assertions.assertEquals("{\"errors\":[\"Cinema id is required\"]}", Objects.requireNonNull(response.getBody()));

        hall.getCinema().setId(1L);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertEquals("{\"errors\":[\"Cinema does not exist\"]}", Objects.requireNonNull(response.getBody()));

        Cinema temp = new Cinema();
        temp.setName("H");
        cinemaService.addCinema(temp);
        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall", HttpMethod.POST, hall, token);
        Assertions.assertEquals("{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":null}}",
                Objects.requireNonNull(response.getBody()));

        Assertions.assertEquals(100, seatService.getSeatsByHallId(1L).size());
    }

    @Test
    public void getNextAvailableSeat() {
        String token = TestUtils.getJwtToken(restTemplate, port);

        Hall hall = new Hall();
        hall.setName("H");
        hall.setNumberOfColumns(10);
        hall.setNumberOfRows(10);

        Cinema temp = new Cinema();
        temp.setName("H");
        hall.setCinema(cinemaService.addCinema(temp));
        hallService.addHall(hall);

        ResponseEntity<String> response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat",
                HttpMethod.GET, null, token);
        Assertions.assertEquals("{\"id\":56,\"positionX\":5,\"positionY\":5,\"status\":\"NOT_USED\",\"relatedSeatIds\":[]," +
                "\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}",
                response.getBody());

        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat",
                HttpMethod.GET, null, token);
        Assertions.assertEquals("{\"id\":56,\"positionX\":5,\"positionY\":5,\"status\":\"NOT_USED\",\"relatedSeatIds\":[]," +
                        "\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}",
                response.getBody());

        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat",
                HttpMethod.POST, null, token);
        Assertions.assertEquals("{\"id\":56,\"positionX\":5,\"positionY\":5,\"status\":\"USED\",\"relatedSeatIds\":[]," +
                        "\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}",
                response.getBody());

        Seat[][] hallSeatsAsGrid = seatService.getHallSeatsAsGrid(hall);
        Assertions.assertEquals(hallSeatsAsGrid[4][4].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[4][5].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[4][6].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[5][4].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[5][6].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[6][4].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[6][5].getStatus(), SeatStatus.DISTANCE);
        Assertions.assertEquals(hallSeatsAsGrid[6][6].getStatus(), SeatStatus.DISTANCE);

        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat",
                HttpMethod.GET, null, token);
        Assertions.assertEquals("{\"id\":36,\"positionX\":3,\"positionY\":5,\"status\":\"NOT_USED\",\"relatedSeatIds\":[]," +
                        "\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}",
                response.getBody());

        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat?positionX=1&positionY=1",
                HttpMethod.POST, null, token);
        Assertions.assertEquals("{\"id\":1,\"positionX\":0,\"positionY\":0,\"status\":\"USED\",\"relatedSeatIds\":[]," +
                        "\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}",
                response.getBody());

        response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat?positionX=1&positionY=1",
                HttpMethod.POST, null, token);
        Assertions.assertEquals("{\"id\":23,\"positionX\":2,\"positionY\":2,\"status\":\"USED\",\"relatedSeatIds\":[]," +
                        "\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}",
                response.getBody());
    }

    @Test
    public void getNextAvailableSeatParallelBook() {
        String token = TestUtils.getJwtToken(restTemplate, port);

        Hall hall = new Hall();
        hall.setName("H");
        hall.setNumberOfColumns(10);
        hall.setNumberOfRows(10);

        Cinema temp = new Cinema();
        temp.setName("H");
        hall.setCinema(cinemaService.addCinema(temp));
        hallService.addHall(hall);

        List<ResponseEntity<String>> responseList = new ArrayList<>();

        Arrays.stream(new Integer[10]).parallel().forEach(e -> {
            ResponseEntity<String> response = TestUtils.sendRequest(restTemplate, "http://localhost:" + port + "/hall/1/nextAvailableSeat",
                    HttpMethod.POST, null, token);
            responseList.add(response);
        });

        responseList.sort(Comparator.comparing(o -> Objects.requireNonNull(o.getBody())));

        Assertions.assertEquals("{\"id\":16,\"positionX\":1,\"positionY\":5,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(0).getBody());
        Assertions.assertEquals("{\"id\":34,\"positionX\":3,\"positionY\":3,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(1).getBody());
        Assertions.assertEquals("{\"id\":36,\"positionX\":3,\"positionY\":5,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(2).getBody());
        Assertions.assertEquals("{\"id\":38,\"positionX\":3,\"positionY\":7,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(3).getBody());
        Assertions.assertEquals("{\"id\":54,\"positionX\":5,\"positionY\":3,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(4).getBody());
        Assertions.assertEquals("{\"id\":56,\"positionX\":5,\"positionY\":5,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(5).getBody());
        Assertions.assertEquals("{\"id\":58,\"positionX\":5,\"positionY\":7,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(6).getBody());
        Assertions.assertEquals("{\"id\":74,\"positionX\":7,\"positionY\":3,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(7).getBody());
        Assertions.assertEquals("{\"id\":76,\"positionX\":7,\"positionY\":5,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(8).getBody());
        Assertions.assertEquals("{\"id\":78,\"positionX\":7,\"positionY\":7,\"status\":\"USED\",\"relatedSeatIds\":[],\"hall\":{\"id\":1,\"name\":\"H\",\"numberOfRows\":10,\"numberOfColumns\":10,\"cinema\":{\"id\":1,\"name\":\"H\"}}}", responseList.get(9).getBody());
    }
}