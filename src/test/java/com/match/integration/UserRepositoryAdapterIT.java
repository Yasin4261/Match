package com.match.integration;

import com.match.domain.port.out.UserRepositoryPort;
import com.match.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryAdapterIT extends AbstractPostgresIT {

    @Autowired UserRepositoryPort users;

    @Test
    void save_and_retrieve_by_email() {
        User u = User.newUser("it-" + UUID.randomUUID() + "@x.com", "HASH");
        User saved = users.save(u);
        assertEquals(u.id(), saved.id());

        assertTrue(users.findByEmail(u.email()).isPresent());
        assertTrue(users.existsByEmail(u.email()));
        assertFalse(users.existsByEmail("missing-" + UUID.randomUUID() + "@x.com"));
    }
}

