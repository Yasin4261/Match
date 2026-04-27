package com.match.application.discovery;

import com.match.domain.geo.GeoLocation;
import com.match.domain.port.out.ProfileRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscoveryServiceTest {

    @Mock ProfileRepositoryPort profiles;
    @InjectMocks DiscoveryService service;

    @Test
    void clamps_limit_to_max_100() {
        when(profiles.discoverNearby(any(), any(), anyDouble(), eq(100))).thenReturn(List.of());
        service.discover(UUID.randomUUID(), new GeoLocation(0, 0), 10, 999);
        verify(profiles).discoverNearby(any(), any(), eq(10.0), eq(100));
    }

    @Test
    void passes_through_when_below_max() {
        ArgumentCaptor<Integer> limit = ArgumentCaptor.forClass(Integer.class);
        when(profiles.discoverNearby(any(), any(), anyDouble(), limit.capture())).thenReturn(List.of());
        service.discover(UUID.randomUUID(), new GeoLocation(0, 0), 10, 25);
        verify(profiles).discoverNearby(any(), any(), anyDouble(), eq(25));
    }
}

