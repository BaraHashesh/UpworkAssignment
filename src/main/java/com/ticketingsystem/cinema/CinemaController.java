package com.ticketingsystem.cinema;

import com.ticketingsystem.error.RequestValidationException;
import com.ticketingsystem.hall.Hall;
import com.ticketingsystem.hall.HallRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/cinema")
public class CinemaController {

    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;

    @Autowired
    public CinemaController(CinemaRepository cinemaRepository, HallRepository hallRepository) {
        this.cinemaRepository = cinemaRepository;
        this.hallRepository = hallRepository;
    }

    @GetMapping()
    public ResponseEntity<Object> listCinema() {
        return ResponseEntity.status(HttpStatus.OK).body(cinemaRepository.findAll());
    }

    @GetMapping(path = "{cinemaId}/halls")
    public ResponseEntity<List<Hall>> listCinemaHalls(@PathVariable Long cinemaId) {
        Cinema cinema = cinemaRepository.findById(cinemaId).orElse(null);
        if (cinema == null) {
            throw new RequestValidationException("Cinema not found for the given Id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(hallRepository.findHallsByCinemaId(cinemaId));
    }

    @PostMapping()
    public ResponseEntity<Cinema> addCinema(@Valid @RequestBody Cinema cinema) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cinemaRepository.save(cinema));
    }
}
