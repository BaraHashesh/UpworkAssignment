package com.ticketingsystem.repositories.seat;

import com.ticketingsystem.models.seat.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Basic Repository for CRUD operations over Seat DAO
 */
@Transactional
public interface SeatRepository extends CrudRepository<Seat, Integer> {

    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId")
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findSeatsByHallId(@Param("hallId") Long hallId);

    @Modifying
    @Query("UPDATE Seat s SET s.relatedSeatIds = '', s.status = 'NOT_USED' WHERE s.hall.id = :hallId")
    void clearSeats(@Param("hallId") Long hallId);
}
