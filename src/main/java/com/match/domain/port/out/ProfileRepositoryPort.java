package com.match.domain.port.out;

import com.match.domain.profile.Profile;
import com.match.domain.geo.GeoLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepositoryPort {
    Profile save(Profile profile);
    Optional<Profile> findByUserId(UUID userId);
    List<Profile> discoverNearby(UUID userId, GeoLocation center, double radiusKm, int limit);
}

