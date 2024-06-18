package com.ticketingsystem.services.hall;

import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;
import com.ticketingsystem.repositories.cinema.CinemaRepository;
import com.ticketingsystem.repositories.hall.HallRepository;
import com.ticketingsystem.services.seat.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Concrete implementation for the HallService
 */
@Transactional
@Service
public class HallServiceImp implements HallService {
    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;
    private final SeatService seatService;

    @Autowired
    public HallServiceImp(CinemaRepository cinemaRepository, HallRepository hallRepository, SeatService seatService) {
        this.cinemaRepository = cinemaRepository;
        this.hallRepository = hallRepository;
        this.seatService = seatService;
    }


    @Override
    public Cinema getCinemaById(Long cinemaId) {
        return this.cinemaRepository.findById(cinemaId).orElse(null);
    }

    @Override
    public Hall getHallById(Long hallId) {
        return this.hallRepository.findById(hallId).orElse(null);
    }

    @Override
    public Hall addHall(Hall hall) {
        Hall createdHall =  this.hallRepository.save(hall);
        this.seatService.generateSeats(createdHall);
        return createdHall;
    }

    @Override
    public List<Hall> findAll() {
        return StreamSupport.stream(this.hallRepository.findAll().spliterator(), false).toList();
    }

    @Override
    public List<Seat> getHallSeats(Hall hall) {
        return this.seatService.getSeatsByHallId(hall.getId());
    }

    @Override
    public Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY) {
        return this.seatService.getNextAvailableSeat(hall, positionX, positionY);
    }

    @Override
    public Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY) {
        return this.seatService.bookNextAvailableSeat(hall, positionX, positionY);
    }
}
