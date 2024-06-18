package com.ticketingsystem.models.cinema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
@Entity
@Table(name = "cinema")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "Cinema Id should not be provided in the request body")
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Cinema Name is mandatory")
    @Column(name = "name", columnDefinition = "varchar(191)")
    private String name;
}
