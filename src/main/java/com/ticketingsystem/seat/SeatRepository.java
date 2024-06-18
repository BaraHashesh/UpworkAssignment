package com.ticketingsystem.seat;

import com.ticketingsystem.hall.Hall;

import java.util.List;

public interface SeatRepository {

    void generateSeats(Hall hall);

    List<Seat> getSeatsByHallId(Long hallId);

    Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);

    Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY, Seat[][] seatGrid);

    Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);
}
