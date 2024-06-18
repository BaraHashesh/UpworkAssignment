package com.ticketingsystem.models.hall;

import com.ticketingsystem.models.cinema.Cinema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
@Entity
@Table(name = "hall")
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "ID must not be included in the request body")
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Name is mandatory for the hall")
    @Column(name = "name", columnDefinition = "varchar(191)")
    private String name;

    @NotNull(message = "Number of Rows is required")
    @Min(value = 1, message = "Value can't be smaller than 1")
    @Column(name = "number_of_rows")
    private Integer numberOfRows;

    @NotNull(message = "Number of columns is required")
    @Min(value = 1, message = "Value can't be smaller than 1")
    @Column(name = "number_of_columns")
    private Integer numberOfColumns;

    @NotNull(message = "Cinema is required for hall")
    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
}
