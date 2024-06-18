package com.ticketingsystem.services.seat;

import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;

import java.util.List;

public interface SeatService {

    void generateSeats(Hall hall);

    List<Seat> getSeatsByHallId(Long hallId);

    Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);

    Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);

    Seat[][] getHallSeatsAsGrid(Hall hall);
}
