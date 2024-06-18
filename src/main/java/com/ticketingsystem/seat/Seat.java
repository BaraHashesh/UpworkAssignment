package com.ticketingsystem.seat;

import com.ticketingsystem.hall.Hall;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Data
@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer positionX;

    private Integer positionY;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Convert(converter = IdListConverter.class)
    @Column(name = "related_seats")
    private List<Long> relatedSeatIds;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Hall hall;
}
