package com.match.domain.port.in;

import com.match.domain.geo.GeoLocation;
import com.match.domain.profile.Profile;

import java.util.List;
import java.util.UUID;

public interface DiscoveryUseCase {
    List<Profile> discover(UUID userId, GeoLocation center, double radiusKm, int limit);
}

