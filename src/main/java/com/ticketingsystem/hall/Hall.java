package com.ticketingsystem.hall;

import com.ticketingsystem.cinema.Cinema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
@Entity
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "ID must not be included in the request body")
    private Long id;

    @NotBlank(message = "Name is mandatory for the hall")
    private String name;

    @NotNull(message = "Number of Rows is required")
    @Min(value = 1, message = "Value can't be smaller than 1")
    private Integer numberOfRows;

    @NotNull(message = "Number of columns is required")
    @Min(value = 1, message = "Value can't be smaller than 1")
    private Integer numberOfColumns;

    @NotNull(message = "Cinema is required for hall")
    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
}
