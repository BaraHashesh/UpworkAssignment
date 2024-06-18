package com.ticketingsystem.repositories.hall;

import com.ticketingsystem.models.hall.Hall;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Basic Repository for CRUD operations over Hall DAO
 */
@Transactional
public interface HallRepository extends CrudRepository<Hall, Long> {

    @Query("SELECT h FROM Hall h WHERE h.cinema.id = :cinemaId")
    List<Hall> findHallsByCinemaId(@Param("cinemaId") Long cinemaId);
}
