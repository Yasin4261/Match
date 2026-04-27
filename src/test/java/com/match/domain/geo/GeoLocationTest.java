package com.match.domain.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationTest {

    @Test
    void accepts_valid_coords() {
        GeoLocation g = new GeoLocation(41.0, 29.0);
        assertEquals(41.0, g.latitude());
        assertEquals(29.0, g.longitude());
    }

    @Test
    void rejects_out_of_range_lat() {
        assertThrows(IllegalArgumentException.class, () -> new GeoLocation(-91.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new GeoLocation(91.0, 0));
    }

    @Test
    void rejects_out_of_range_lng() {
        assertThrows(IllegalArgumentException.class, () -> new GeoLocation(0, -181.0));
        assertThrows(IllegalArgumentException.class, () -> new GeoLocation(0, 181.0));
    }
}

