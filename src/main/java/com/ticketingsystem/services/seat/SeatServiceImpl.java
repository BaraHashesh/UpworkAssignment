package com.ticketingsystem.services.seat;

import com.ticketingsystem.models.hall.Hall;
import com.ticketingsystem.models.seat.Seat;
import com.ticketingsystem.models.seat.SeatStatus;
import com.ticketingsystem.repositories.seat.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SeatServiceImpl implements SeatService {

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

    private final SeatRepository seatRepository;

    @Autowired
    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public void generateSeats(Hall hall) {
        List<Seat> seats = new ArrayList<>();

        // Generate all seats for Hall
        for (int i = 0; i < hall.getNumberOfRows(); i++) {
            for (int j = 0; j < hall.getNumberOfColumns(); j++) {
                Seat seat = new Seat();
                seat.setPositionX(i);
                seat.setPositionY(j);
                seat.setHall(hall);
                seat.setStatus(SeatStatus.NOT_USED);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    @Override
    public Seat getNextAvailableSeat(Hall hall, Integer positionX, Integer positionY) {
        boolean[][] visited = new boolean[hall.getNumberOfRows()][hall.getNumberOfColumns()];
        Seat[][] seatGrid = this.getHallSeatsAsGrid(hall);

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
            List<Seat> availableSeats = new ArrayList<>(expandedSeats.stream().filter(seat -> isSeatAvailable(hall, seat, seatGrid)).toList());
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
            expandedSeats.forEach(seat -> furtherExpandedSeats.addAll(getSurroundingSeats(hall, seat, seatGrid, visited)));
            expandedSeats = furtherExpandedSeats;
        }
        return null;
    }

    @Override
    public Seat bookNextAvailableSeat(Hall hall, Integer positionX, Integer positionY) {
        Seat seat = this.getNextAvailableSeat(hall, positionX, positionY);
        if (seat == null) {
            return null;
        }
        seat.setStatus(SeatStatus.USED);
        this.seatRepository.save(seat);

        List<Seat> surroundingSeats = this.getSurroundingSeats(hall, seat, this.getHallSeatsAsGrid(hall),
                new boolean[hall.getNumberOfRows()][hall.getNumberOfColumns()]);
        for (Seat surroundingSeat : surroundingSeats) {
            surroundingSeat.getRelatedSeatIds().add(seat.getId());

            if (surroundingSeat.getStatus() != SeatStatus.USED) {
                surroundingSeat.setStatus(SeatStatus.DISTANCE);
            }
        }
        this.seatRepository.saveAll(surroundingSeats);
        return seat;
    }

    private Seat[][] getHallSeatsAsGrid(Hall hall) {
        Seat[][] seatGrid = new Seat[hall.getNumberOfRows()][hall.getNumberOfColumns()];
        List<Seat> seats = seatRepository.findSeatsByHallId(hall.getId());
        seats.forEach(seat -> seatGrid[seat.getPositionX()][seat.getPositionY()] = seat);
        return seatGrid;
    }

    private boolean isSeatAvailable(Hall hall, Seat seat, Seat[][] seatGrid) {

        if (seat.getStatus() != SeatStatus.NOT_USED) {
            return false;
        }

        for (Pair<Integer, Integer> position : SURROUNDING_POSITIONS) {
            int positionX = seat.getPositionX() + position.getFirst();
            int positionY = seat.getPositionY() + position.getSecond();

            // Seat doesn't exist
            if (isSeatOutOfBoundary(hall, positionX, positionY)) {
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
            if (isSeatOutOfBoundary(hall, positionX, positionY)) {
                continue;
            }

            if (!visited[positionX][positionY]){
                surroundingSeats.add(seatGrid[positionX][positionY]);
                visited[positionX][positionY] = true;
            }

        }
        return surroundingSeats;
    }

    private static boolean isSeatOutOfBoundary(Hall hall, int positionX, int positionY) {
        return positionX >= hall.getNumberOfRows() || positionY >= hall.getNumberOfColumns() || positionX < 0 || positionY < 0;
    }
}
