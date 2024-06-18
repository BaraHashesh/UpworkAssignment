package com.ticketingsystem.services.cinema;

import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.repositories.cinema.CinemaRepository;
import com.ticketingsystem.repositories.hall.HallRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Concrete implementation for the CinemaService
 */
@Transactional
@Service
public class CinemaServiceImp implements CinemaService {

    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;

    @Autowired
    public CinemaServiceImp(CinemaRepository cinemaRepository, HallRepository hallRepository) {
        this.cinemaRepository = cinemaRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public Cinema findById(Long cinemaId) {
        return cinemaRepository.findById(cinemaId).orElse(null);
    }

    @Override
    public List<Cinema> findAll() {
        return StreamSupport.stream(cinemaRepository.findAll().spliterator(), false).toList();
    }

    @Override
    public Cinema addCinema(Cinema cinema) {
        return cinemaRepository.save(cinema);
    }

    @Override
    public List<Hall> findHallsByCinemaId(Long cinemaId) {
        return hallRepository.findHallsByCinemaId(cinemaId);
    }
}
