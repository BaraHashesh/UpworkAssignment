package com.ticketingsystem.seat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatCrudRepository extends CrudRepository<Seat, Integer> {

    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId")
    List<Seat> findSeatsByHallId(@Param("hallId") Long hallId);
}
