package com.match.adapter.rest.discovery;

import com.match.adapter.rest.security.CurrentUser;
import com.match.domain.geo.GeoLocation;
import com.match.domain.port.in.DiscoveryUseCase;
import com.match.domain.profile.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discovery")
public class DiscoveryController {

    private final DiscoveryUseCase discovery;

    public DiscoveryController(DiscoveryUseCase discovery) { this.discovery = discovery; }

    @GetMapping
    public List<Profile> discover(@RequestParam double lat,
                                  @RequestParam double lng,
                                  @RequestParam(defaultValue = "50") double radiusKm,
                                  @RequestParam(defaultValue = "20") int limit) {
        return discovery.discover(CurrentUser.id(), new GeoLocation(lat, lng), radiusKm, limit);
    }
}

