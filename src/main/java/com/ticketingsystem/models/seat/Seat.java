package com.ticketingsystem.models.seat;

import com.ticketingsystem.models.hall.Hall;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "position_x")
    private Integer positionX;

    @Column(name = "position_y")
    private Integer positionY;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SeatStatus status;

    @Convert(converter = IdListConverter.class)
    @Column(name = "related_seats")
    private List<Long> relatedSeatIds;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;
}
