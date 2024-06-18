package com.ticketingsystem.repositories.cinema;

import com.ticketingsystem.models.cinema.Cinema;
import org.springframework.data.repository.CrudRepository;

/**
 * Basic Repository for CRUD operations over Cinema DAO
 */
public interface CinemaRepository extends CrudRepository<Cinema, Long> {
}
