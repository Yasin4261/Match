package com.match.application.discovery;

import com.match.domain.geo.GeoLocation;
import com.match.domain.port.in.DiscoveryUseCase;
import com.match.domain.port.out.ProfileRepositoryPort;
import com.match.domain.profile.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DiscoveryService implements DiscoveryUseCase {

    private final ProfileRepositoryPort profiles;

    public DiscoveryService(ProfileRepositoryPort profiles) {
        this.profiles = profiles;
    }

    @Override
    public List<Profile> discover(UUID userId, GeoLocation center, double radiusKm, int limit) {
        return profiles.discoverNearby(userId, center, radiusKm, Math.min(limit, 100));
    }
}

