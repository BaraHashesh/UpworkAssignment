package com.ticketingsystem.controllers.hall;

import com.ticketingsystem.exceptions.RequestValidationException;
import com.ticketingsystem.exceptions.ResourceNotAvailableException;
import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;
import com.ticketingsystem.services.hall.HallService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle Rest requests linked to the Hall DAO
 * This includes the operations of getting the next seat & booking it
 */
@RestController
@RequestMapping("/hall")
public class HallController {

    private final HallService hallService;

    @Autowired
    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @PostMapping()
    @Transactional
    public ResponseEntity<Hall> createHall(@Valid @RequestBody Hall hall) {
        // Request Validation
        if (hall.getCinema() == null || hall.getCinema().getId() == null) {
            throw new RequestValidationException("Cinema id is required");
        }

        if (this.hallService.getCinemaById(hall.getCinema().getId()) == null) {
            throw new RequestValidationException("Cinema does not exist");
        }

        Hall createdHall = hallService.addHall(hall);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHall);
    }

    @GetMapping()
    public ResponseEntity<List<Hall>> listHalls() {
        return ResponseEntity.status(HttpStatus.OK).body(this.hallService.findAll());
    }

    @GetMapping("{hallId}/seats")
    public ResponseEntity<List<Seat>> getHallSeats(@PathVariable long hallId) {
        Hall hall = this.hallService.getHallById(hallId);
        if (hall == null) {
            throw new RequestValidationException("Selected Hall not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.hallService.getHallSeats(hall));
    }

    @GetMapping("{hallId}/nextAvailableSeat")
    public ResponseEntity<Seat> getNextAvailableSeat(@PathVariable Long hallId,
                                                       @RequestParam(required = false) Integer positionX,
                                                       @RequestParam(required = false) Integer positionY) {
        Hall hall = validateHallDetails(hallId, positionX, positionY);

        Seat seat = this.hallService.getNextAvailableSeat(hall,
                positionX == null ? hall.getNumberOfRows() / 2 : positionX - 1,
                positionY == null ? hall.getNumberOfColumns() / 2 : positionY - 1);

        if (seat == null) {
            throw new ResourceNotAvailableException("No available seat found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(seat);
    }

    @PostMapping("{hallId}/nextAvailableSeat")
    public ResponseEntity<Seat> bookNextAvailableSeat(@PathVariable Long hallId,
                                                       @RequestParam(required = false) Integer positionX,
                                                       @RequestParam(required = false) Integer positionY) {
        Hall hall = validateHallDetails(hallId, positionX, positionY);
        Seat seat = this.hallService.bookNextAvailableSeat(hall,
                positionX == null ? hall.getNumberOfRows() / 2 : positionX - 1,
                positionY == null ? hall.getNumberOfColumns() / 2 : positionY - 1);
        if (seat == null) {
            throw new ResourceNotAvailableException("No available seat found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(seat);
    }

    @PostMapping("{hallId}/clearSeats")
    public ResponseEntity<String> clearSeats(@PathVariable Long hallId) {
        Hall hall = this.hallService.getHallById(hallId);
        if (hall == null) {
            throw new RequestValidationException("Selected Hall not found");
        }
        this.hallService.clearSeats(hall);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    /**
     * Common validation function to validate that the Hall exists & the specified position exists wihtin the hall
     * @param hallId The ID of the Hall
     * @param positionX The Row index within the hall
     * @param positionY The Column index within the hall
     * @return The hall DAO if the info is valid
     */
    private Hall validateHallDetails(Long hallId, Integer positionX, Integer positionY) {
        if ((positionX != null && positionX <= 0) || (positionY != null && positionY <= 0)) {
            throw new RequestValidationException("Both positionX & positionY Request Query Params should be set to positive values");
        }

        Hall hall = this.hallService.getHallById(hallId);
        if (hall == null) {
            throw new RequestValidationException("Selected Hall not found");
        }

        if ((positionX != null && hall.getNumberOfRows() < positionX) || (positionY != null && hall.getNumberOfColumns() < positionY)) {
            String message = String.format("Position X & Y should be within then possible values for the Rows (%d) & Columns (%d)",
                    hall.getNumberOfRows(), hall.getNumberOfColumns());
            throw new RequestValidationException(message);
        }
        return hall;
    }
}
