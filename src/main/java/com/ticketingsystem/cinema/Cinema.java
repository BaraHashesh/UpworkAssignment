package com.ticketingsystem.cinema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
@Entity
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "Cinema Id should not be provided in the request body")
    private Long id;

    @NotBlank(message = "Cinema Name is mandatory")
    private String name;
}
