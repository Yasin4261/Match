package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.ProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProfileSpringDataRepository extends JpaRepository<ProfileJpaEntity, UUID> {

    /**
     * Geo discovery using PostGIS ST_DWithin on geography column (meters).
     * Excludes self and previously swiped users.
     */
    @Query(value = """
        SELECT p.* FROM profiles p
        WHERE p.user_id <> :userId
          AND ST_DWithin(p.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radiusMeters)
          AND NOT EXISTS (
            SELECT 1 FROM swipes s WHERE s.swiper_id = :userId AND s.swipee_id = p.user_id
          )
        ORDER BY p.location <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        LIMIT :lim
        """, nativeQuery = true)
    List<ProfileJpaEntity> discoverNearby(@Param("userId") UUID userId,
                                          @Param("lat") double lat,
                                          @Param("lng") double lng,
                                          @Param("radiusMeters") double radiusMeters,
                                          @Param("lim") int limit);
}

