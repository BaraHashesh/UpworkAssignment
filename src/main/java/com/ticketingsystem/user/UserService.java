package com.ticketingsystem.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class UserService implements UserRepository {

    private final EntityManager entityManager;

    @Autowired
    public UserService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email).getResultStream().findFirst().orElse(null);
    }

    @Transactional
    @Override
    public User add(User user) {
        User dbUser = this.findByEmail(user.getEmail());

        if (dbUser == null) {
            entityManager.persist(user);
            entityManager.flush();
            return this.findByEmail(user.getEmail());
        }
        dbUser.setDeleted(false);
        entityManager.flush();
        return dbUser;
    }

    @Override
    public void delete(UUID id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", id);

        query.getResultStream().findFirst().ifPresent(user -> user.setDeleted(true));
        entityManager.flush();
    }
}
