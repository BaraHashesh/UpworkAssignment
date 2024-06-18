package com.ticketingsystem.seat;

import com.ticketingsystem.hall.Hall;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SeatService implements SeatRepository {

    private static final List<Pair<Integer, Integer>> SURROUNDING_POSITIONS = new ArrayList<>() {{
        add(Pair.of(-1 , -1));
        add(Pair.of(-1 , 0));
        add(Pair.of(-1 , 1));
        add(Pair.of(0 , -1));
        add(Pair.of(0 , 1));
        add(Pair.of(1 , -1));
        add(Pair.of(1 , 0));
        add(Pair.of(1 , 1));
    }};

    private final SeatCrudRepository seatCrudRepository;

    @Autowired
    public SeatService(SeatCrudRepository seatCrudRepository) {
        this.seatCrudRepository = seatCrudRepository;
    }

    @Override
    public void generateSeats(Hall hall) {
        // Generate all seats for Hall
        for (int i = 0; i < hall.getNumberOfRows(); i++) {
            for (int j = 0; j < hall.getNumberOfColumns(); j++) {
                Seat seat = new Seat();
                seat.setPositionX(i);
                seat.setPositionY(j);
                seat.setHall(hall);
                seat.setStatus(SeatStatus.NOT_USED);
                this.seatCrudRepository.save(seat);
            }
        }
    }

    @Override
    public List<Seat> getSeatsByHallId(Long hallId) {
        return seatCrudRepository.findSeatsByHallId(hallId);
    }

    @Override
    public Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY) {
        return this.getNextAvailableSeat(hall, positionX, positionY, null);
    }

    @Override
    public Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY, Seat[][] seatGrid) {
        boolean[][] visited = new boolean[hall.getNumberOfRows()][hall.getNumberOfColumns()];

        if (seatGrid == null) {
            List<Seat> seats = seatCrudRepository.findSeatsByHallId(hall.getId());
            seatGrid = new Seat[hall.getNumberOfRows()][hall.getNumberOfColumns()];
            Seat[][] finalSeatGrid = seatGrid;
            seats.forEach(seat -> finalSeatGrid[seat.getPositionX()][seat.getPositionY()] = seat);
        }

        // Requested Seat is available
        if (isSeatAvailable(hall, seatGrid[positionX][positionY], seatGrid)) {
            return seatGrid[positionX][positionY];
        }

        visited[positionX][positionY] = true;

        // Search for next available seat
        Integer middlePointX = hall.getNumberOfRows() / 2;
        Integer middlePointY = hall.getNumberOfColumns() / 2;

        // Get surrounding seats
        List<Seat> expandedSeats = getSurroundingSeats(hall, seatGrid[positionX][positionY], seatGrid, visited);

        // Expand Grid in BFS approach
        while (!expandedSeats.isEmpty()) {
            // Find if any of the current elements is available
            Seat[][] finalSeatGrid1 = seatGrid;
            List<Seat> availableSeats = new ArrayList<>(expandedSeats.stream().filter(seat -> isSeatAvailable(hall, seat, finalSeatGrid1)).toList());
            if (!availableSeats.isEmpty()) {
                // Sort relative to distance of middle seat
                availableSeats.sort((seat1, seat2) -> {
                    Integer distanceSeat1 = Math.abs(middlePointX - seat1.getPositionX()) + Math.abs(middlePointY - seat1.getPositionY());
                    Integer distanceSeat2 = Math.abs(middlePointX - seat2.getPositionX()) + Math.abs(middlePointY - seat2.getPositionY());
                    return distanceSeat1.compareTo(distanceSeat2);
                });
                // get closest seat
                return availableSeats.get(0);
            }

            // Do Expand for the next iteration
            List<Seat> furtherExpandedSeats = new ArrayList<>();
            Seat[][] finalSeatGrid2 = seatGrid;
            expandedSeats.forEach(seat -> furtherExpandedSeats.addAll(getSurroundingSeats(hall, seat, finalSeatGrid2, visited)));
            expandedSeats = furtherExpandedSeats;
        }
        return null;
    }

    @Override
    public Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY) {
        List<Seat> seats = seatCrudRepository.findSeatsByHallId(hall.getId());
        Seat[][] seatGrid = new Seat[hall.getNumberOfRows()][hall.getNumberOfColumns()];
        seats.forEach(seat -> seatGrid[seat.getPositionX()][seat.getPositionY()] = seat);

        Seat seat = this.getNextAvailableSeat(hall, positionX, positionY, seatGrid);
        if (seat == null) {
            return null;
        }
        seat.setStatus(SeatStatus.USED);
        this.seatCrudRepository.save(seat);

        List<Seat> surroundingSeats = this.getSurroundingSeats(hall, seat, seatGrid, null);
        for (Seat surroundingSeat : surroundingSeats) {
            surroundingSeat.getRelatedSeatIds().add(seat.getId());

            if (surroundingSeat.getStatus() != SeatStatus.USED) {
                surroundingSeat.setStatus(SeatStatus.DISTANCE);
            }
            this.seatCrudRepository.save(surroundingSeat);
        }
        return seat;
    }

    private boolean isSeatAvailable(Hall hall, Seat seat, Seat[][] seatGrid) {

        if (seat.getStatus() != SeatStatus.NOT_USED) {
            return false;
        }

        for (Pair<Integer, Integer> position : SURROUNDING_POSITIONS) {
            int positionX = seat.getPositionX() + position.getFirst();
            int positionY = seat.getPositionY() + position.getSecond();

            // Seat doesn't exist
            if (positionX >= hall.getNumberOfRows() ||  positionY >= hall.getNumberOfColumns() || positionX < 0 || positionY < 0) {
                continue;
            }

            if (seatGrid[positionX][positionY].getStatus() == SeatStatus.USED) {
               return false;
            }
        }
        return true;
    }

    private List<Seat> getSurroundingSeats(Hall hall, Seat seat, Seat[][] seatGrid, boolean[][] visited) {
        List<Seat> surroundingSeats = new ArrayList<>();

        for (Pair<Integer, Integer> position : SURROUNDING_POSITIONS) {
            int positionX = seat.getPositionX() + position.getFirst();
            int positionY = seat.getPositionY() + position.getSecond();

            // Seat doesn't exist
            if (positionX >= hall.getNumberOfRows() ||  positionY >= hall.getNumberOfColumns() || positionX < 0 || positionY < 0) {
                continue;
            }

            if (visited == null) {
                surroundingSeats.add(seatGrid[positionX][positionY]);
            } else if (!visited[positionX][positionY]){
                surroundingSeats.add(seatGrid[positionX][positionY]);
                visited[positionX][positionY] = true;
            }

        }
        return surroundingSeats;
    }
}
