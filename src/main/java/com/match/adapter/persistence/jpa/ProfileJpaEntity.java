package com.match.adapter.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileJpaEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String displayName;
    private LocalDate birthDate;
    private String gender;
    @Column(length = 1000)
    private String bio;

    /** PostGIS geography(Point,4326) — see V1 migration. */
    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    private Instant lastActiveAt;
}

