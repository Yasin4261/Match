package com.match.adapter.persistence.adapter;

import com.match.adapter.persistence.jpa.ProfileJpaEntity;
import com.match.adapter.persistence.repository.ProfileSpringDataRepository;
import com.match.domain.geo.GeoLocation;
import com.match.domain.port.out.ProfileRepositoryPort;
import com.match.domain.profile.Gender;
import com.match.domain.profile.Profile;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfileRepositoryAdapter implements ProfileRepositoryPort {

    private static final GeometryFactory GEO = new GeometryFactory(new PrecisionModel(), 4326);
    private final ProfileSpringDataRepository repo;

    public ProfileRepositoryAdapter(ProfileSpringDataRepository repo) { this.repo = repo; }

    @Override public Profile save(Profile p) {
        Point pt = null;
        if (p.location() != null) {
            pt = GEO.createPoint(new Coordinate(p.location().longitude(), p.location().latitude()));
            pt.setSRID(4326);
        }
        ProfileJpaEntity e = ProfileJpaEntity.builder()
            .userId(p.userId()).displayName(p.displayName()).birthDate(p.birthDate())
            .gender(p.gender() == null ? null : p.gender().name())
            .bio(p.bio()).location(pt).lastActiveAt(p.lastActiveAt()).build();
        return toDomain(repo.save(e));
    }

    @Override public Optional<Profile> findByUserId(UUID userId) {
        return repo.findById(userId).map(this::toDomain);
    }

    @Override public List<Profile> discoverNearby(UUID userId, GeoLocation center, double radiusKm, int limit) {
        return repo.discoverNearby(userId, center.latitude(), center.longitude(), radiusKm * 1000, limit)
            .stream().map(this::toDomain).toList();
    }

    private Profile toDomain(ProfileJpaEntity e) {
        GeoLocation loc = null;
        if (e.getLocation() != null) loc = new GeoLocation(e.getLocation().getY(), e.getLocation().getX());
        return new Profile(e.getUserId(), e.getDisplayName(), e.getBirthDate(),
            e.getGender() == null ? null : Gender.valueOf(e.getGender()), e.getBio(), loc, e.getLastActiveAt());
    }
}

