package com.ticketingsystem.dtos.jwt;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JwtRequest {
    private String email;
    private String password;
}
