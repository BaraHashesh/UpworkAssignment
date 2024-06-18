package com.ticketingsystem.services.hall;

import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;

import java.util.List;

/**
 * Wrapper for the functions needed by the Hall controller
 */
public interface HallService {

    /**
     * Get the Cinema object linked to a specific IO
     * @param cinemaId ID to look for
     * @return The cinema object linked to the ID
     */
    Cinema getCinemaById(Long cinemaId);

    /**
     * Get the hall object linked to a specific IO
     * @param hallId ID to look for
     * @return The hall object linked to the ID
     */
    Hall getHallById(Long hallId);

    /**
     * Add a hall object to the DB
     * @param hall hall object to add
     * @return The newly created hall object
     */
    Hall addHall(Hall hall);

    /**
     * List all available halls
     * @return List of hall objects
     */
    List<Hall> findAll();

    /**
     * Get all Seats linked to the Hall
     * @param hall Hall to fetch the seats for
     * @return List of seat objects
     */
    List<Seat> getHallSeats(Hall hall);

    /**
     * Look for the next available Seat in the Hall
     * @param hall Hall to look into
     * @param positionX Initial row used in look up
     * @param positionY Initial column used in look up
     * @return Available Seat Object
     */
    Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);

    /**
     * book the next available Seat in the Hall
     * @param hall Hall to look into
     * @param positionX Initial row used in look up
     * @param positionY Initial column used in look up
     * @return Booked Seat Object
     */
    Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);
}
