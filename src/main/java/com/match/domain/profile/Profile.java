package com.match.domain.profile;

import com.match.domain.geo.GeoLocation;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Profile(
    UUID userId,
    String displayName,
    LocalDate birthDate,
    Gender gender,
    String bio,
    GeoLocation location,
    Instant lastActiveAt
) { }

