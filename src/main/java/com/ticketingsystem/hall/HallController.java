package com.ticketingsystem.hall;

import com.ticketingsystem.cinema.CinemaRepository;
import com.ticketingsystem.error.RequestValidationException;
import com.ticketingsystem.error.ResourceNotAvailableException;
import com.ticketingsystem.seat.Seat;
import com.ticketingsystem.seat.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("hall")
public class HallController {

    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public HallController(CinemaRepository cinemaRepository, HallRepository hallRepository, SeatRepository seatRepository) {
        this.cinemaRepository = cinemaRepository;
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
    }

    @PostMapping()
    public ResponseEntity<Hall> createHall(@RequestBody Hall hall) {
        // Request Validation
        if (hall.getCinema().getId() == null) {
            throw new RequestValidationException("Cinema id is required");
        }

        if (this.cinemaRepository.findById(hall.getCinema().getId()).isEmpty()) {
            throw new RequestValidationException("Cinema does not exist");
        }

        Hall createdHall = hallRepository.save(hall);
        this.seatRepository.generateSeats(createdHall);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHall);
    }

    @GetMapping()
    public ResponseEntity<List<Hall>> listHalls() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(StreamSupport.stream(this.hallRepository.findAll().spliterator(), false).collect(Collectors.toList()));
    }

    @GetMapping("{hallId}/seats")
    public ResponseEntity<List<Seat>> getHallSeats(@PathVariable long hallId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.seatRepository.getSeatsByHallId(hallId));
    }

    @GetMapping("{hallId}/nextAvailableSeat")
    public ResponseEntity<Seat> getNextAvailableSeat(@PathVariable Long hallId,
                                                       @RequestParam(required = false, defaultValue = "1") Integer positionX,
                                                       @RequestParam(required = false, defaultValue = "1") Integer positionY) {
        Hall hall = validateHallDetails(hallId, positionX, positionY);

        Seat seat = this.seatRepository.getNextAvailableSeat(hall, positionX - 1, positionY - 1);
        if (seat == null) {
            throw new ResourceNotAvailableException("No available seat found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(seat);
    }

    @PostMapping("{hallId}/bookNextAvailableSeat")
    public ResponseEntity<Seat> bookNextAvailableSeat(@PathVariable Long hallId,
                                                       @RequestParam(required = false, defaultValue = "1") Integer positionX,
                                                       @RequestParam(required = false, defaultValue = "1") Integer positionY) {
        Hall hall = validateHallDetails(hallId, positionX, positionY);

        Seat seat = this.seatRepository.bookNextAvailableSeat(hall, positionX - 1, positionY - 1);
        if (seat == null) {
            throw new ResourceNotAvailableException("No available seat found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(seat);
    }

    private Hall validateHallDetails(Long hallId, Integer positionX, Integer positionY) {
        if (positionX <= 0 || positionY <= 0) {
            throw new RequestValidationException("Both positionX & positionY Request Query Params should be set to positive values");
        }

        Hall hall = this.hallRepository.findById(hallId).orElse(null);
        if (hall == null) {
            throw new RequestValidationException("Selected Hall not found");
        }

        if (hall.getNumberOfRows() < positionX || hall.getNumberOfColumns() < positionY) {
            String message = String.format("Position X & Y should be within then possible values for the Rows (%d) & Columns (%d)",
                    hall.getNumberOfRows(), hall.getNumberOfColumns());
            throw new RequestValidationException(message);
        }
        return hall;
    }
}
