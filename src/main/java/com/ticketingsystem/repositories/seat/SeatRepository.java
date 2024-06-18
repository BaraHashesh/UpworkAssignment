package com.ticketingsystem.repositories.seat;

import com.ticketingsystem.models.seat.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Basic Repository for CRUD operations over Seat DAO
 */
public interface SeatRepository extends CrudRepository<Seat, Integer> {

    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId")
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findSeatsByHallId(@Param("hallId") Long hallId);
}
