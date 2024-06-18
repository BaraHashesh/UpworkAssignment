package com.ticketingsystem.seat;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatCrudRepository extends CrudRepository<Seat, Integer> {

    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId")
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findSeatsByHallId(@Param("hallId") Long hallId);
}
