package com.ticketingsystem.controllers.cinema;

import com.ticketingsystem.exceptions.RequestValidationException;
import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.services.cinema.CinemaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle requests related to the Cinema DAO
 */
@RestController
@RequestMapping(path="/cinema")
public class CinemaController {

    private final CinemaService cinemaService;

    @Autowired
    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping()
    public ResponseEntity<Cinema> addCinema(@Valid @RequestBody Cinema cinema) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cinemaService.addCinema(cinema));
    }

    @GetMapping()
    public ResponseEntity<Object> listCinema() {
        return ResponseEntity.status(HttpStatus.OK).body(this.cinemaService.findAll());
    }

    @GetMapping(path = "{cinemaId}/halls")
    public ResponseEntity<List<Hall>> listCinemaHalls(@PathVariable Long cinemaId) {
        Cinema cinema = this.cinemaService.findById(cinemaId);
        if (cinema == null) {
            throw new RequestValidationException("Cinema not found for the given Id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.cinemaService.findHallsByCinemaId(cinemaId));
    }
}
