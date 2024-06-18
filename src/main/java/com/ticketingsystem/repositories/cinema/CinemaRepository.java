package com.ticketingsystem.repositories.cinema;

import com.ticketingsystem.models.cinema.Cinema;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Basic Repository for CRUD operations over Cinema DAO
 */
@Transactional
public interface CinemaRepository extends CrudRepository<Cinema, Long> {
}
