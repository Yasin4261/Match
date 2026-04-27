package com.match.integration;

import com.match.domain.geo.GeoLocation;
import com.match.domain.port.out.ProfileRepositoryPort;
import com.match.domain.port.out.UserRepositoryPort;
import com.match.domain.profile.Gender;
import com.match.domain.profile.Profile;
import com.match.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileGeoDiscoveryIT extends AbstractPostgresIT {

    @Autowired UserRepositoryPort users;
    @Autowired ProfileRepositoryPort profiles;

    @Test
    void discovers_only_profiles_within_radius_excluding_self() {
        User me = users.save(User.newUser("me-" + UUID.randomUUID() + "@x.com", "h"));
        User near = users.save(User.newUser("near-" + UUID.randomUUID() + "@x.com", "h"));
        User far = users.save(User.newUser("far-" + UUID.randomUUID() + "@x.com", "h"));

        // Istanbul Taksim
        GeoLocation taksim = new GeoLocation(41.0369, 28.9850);
        // ~3 km away (Beşiktaş)
        GeoLocation besiktas = new GeoLocation(41.0426, 29.0090);
        // ~450 km away (Ankara)
        GeoLocation ankara = new GeoLocation(39.9334, 32.8597);

        profiles.save(new Profile(me.id(), "Me", LocalDate.of(2000, 1, 1),
            Gender.OTHER, "bio", taksim, Instant.now()));
        profiles.save(new Profile(near.id(), "Near", LocalDate.of(2000, 1, 1),
            Gender.OTHER, "bio", besiktas, Instant.now()));
        profiles.save(new Profile(far.id(), "Far", LocalDate.of(2000, 1, 1),
            Gender.OTHER, "bio", ankara, Instant.now()));

        List<Profile> within10km = profiles.discoverNearby(me.id(), taksim, 10, 50);
        assertEquals(1, within10km.size(), "only Near must be within 10km");
        assertEquals(near.id(), within10km.get(0).userId());

        List<Profile> within1000km = profiles.discoverNearby(me.id(), taksim, 1000, 50);
        assertEquals(2, within1000km.size(), "both Near and Far within 1000km, self excluded");
    }
}

