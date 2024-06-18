package com.ticketingsystem.services.seat;

import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;

/**
 * Service used to provide API access on the seat DAO
 */
public interface SeatService {

    /**
     * Generate Seat DAOs for a newly added hall object
     * @param hall Hall for which seat objects are created
     */
    void generateSeats(Hall hall);

    /**
     * Fetch the next available seat within the hall
     * @param hall The hall in which seat is fetched
     * @param positionX The preferred row for the seat
     * @param positionY The preferred column for the seat
     * @return The Next available seat within the Hall
     */
    Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);

    /**
     * Fetch & Book the next available seat within the hall
     * @param hall The hall in which seat is fetched
     * @param positionX The preferred row for the seat
     * @param positionY The preferred column for the seat
     * @return The Next available seat within the Hall
     */
    Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY);
}
