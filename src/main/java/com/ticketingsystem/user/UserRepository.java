package com.ticketingsystem.user;

import java.util.UUID;

/**
 * Base Interface for User actions with the DB
 * This will be implemented by the DataBase service
 */
public interface UserRepository {

    /**
     * Find the user information via the provided email
     * @param email The email of the user
     * @return The user information
     */
    User findByEmail(String email);

    /**
     * Add the user to the DB
     * @param user The user to be added to the DB
     * @return The newly created User object
     */
    User add(User user);

    /**
     * Delete User from the DB
     * @param id The id of the user to be deleted
     */
    void delete(UUID id);
}
