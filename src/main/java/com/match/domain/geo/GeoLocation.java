package com.match.domain.geo;

public record GeoLocation(double latitude, double longitude) {
    public GeoLocation {
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("lat out of range");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("lng out of range");
    }
}

