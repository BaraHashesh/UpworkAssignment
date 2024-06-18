package com.ticketingsystem.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Configuration object to provide the following
 *      1. In memory DB for users
 *      2. Password encryption
 *      3. Authentication Manager
 */
@Configuration
public class Configurations {

    /**
     * Use a simple in memory authorization
     * Can be changed later for a proper Service with register options
     * @return The UserDetailsService to be used for authorization
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder().username("BARA")
                .password(passwordEncoder().encode("1234")).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}