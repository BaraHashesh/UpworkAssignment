package com.ticketingsystem.services.cinema;

import com.ticketingsystem.models.cinema.Cinema;
import com.ticketingsystem.models.hall.Hall;

import java.util.List;

/**
 * Wrapper Service for all functions used by the Cinema Controller to decouple from other DAO repositories/services
 */
public interface CinemaService {

    /**
     * Find the cinema by a Given ID
     * @param cinemaId ID to search for
     * @return The cinema object if it exists in the DB
     */
    Cinema findById(Long cinemaId);

    /**
     * List all cinema DAOs in the DB
     * @return List of cinema DAOs
     */
    List<Cinema> findAll();

    /**
     * Add a cinema object to the DB
     * @param cinema The cinema object to add
     * @return The newly created cinema object
     */
    Cinema addCinema(Cinema cinema);

    /**
     * List all the halls linked to a specific cinema ID
     * @param cinemaId The cinema ID to use
     * @return List of halls linked to the Cinema
     */
    List<Hall> findHallsByCinemaId(Long cinemaId);
}
