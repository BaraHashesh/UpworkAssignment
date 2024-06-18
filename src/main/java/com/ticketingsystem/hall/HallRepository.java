package com.ticketingsystem.hall;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HallRepository extends CrudRepository<Hall, Long> {

    @Query("SELECT h FROM Hall h WHERE h.cinema.id = :cinemaId")
    List<Hall> findHallsByCinemaId(@Param("cinemaId") Long cinemaId);
}
