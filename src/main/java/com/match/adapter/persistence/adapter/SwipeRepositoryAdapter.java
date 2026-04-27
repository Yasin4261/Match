package com.match.adapter.persistence.adapter;

import com.match.adapter.persistence.jpa.SwipeJpaEntity;
import com.match.adapter.persistence.repository.SwipeSpringDataRepository;
import com.match.domain.port.out.SwipeRepositoryPort;
import com.match.domain.swipe.Swipe;
import com.match.domain.swipe.SwipeDirection;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SwipeRepositoryAdapter implements SwipeRepositoryPort {

    private final SwipeSpringDataRepository repo;

    public SwipeRepositoryAdapter(SwipeSpringDataRepository repo) { this.repo = repo; }

    @Override public Swipe save(Swipe s) {
        SwipeJpaEntity e = SwipeJpaEntity.builder()
            .id(s.id()).swiperId(s.swiperId()).swipeeId(s.swipeeId())
            .direction(s.direction().name()).createdAt(s.createdAt()).build();
        return toDomain(repo.save(e));
    }

    @Override public Optional<Swipe> find(UUID swiperId, UUID swipeeId) {
        return repo.findBySwiperIdAndSwipeeId(swiperId, swipeeId).map(this::toDomain);
    }

    @Override public boolean exists(UUID swiperId, UUID swipeeId, SwipeDirection direction) {
        return repo.existsBySwiperIdAndSwipeeIdAndDirection(swiperId, swipeeId, direction.name());
    }

    private Swipe toDomain(SwipeJpaEntity e) {
        return new Swipe(e.getId(), e.getSwiperId(), e.getSwipeeId(),
            SwipeDirection.valueOf(e.getDirection()), e.getCreatedAt());
    }
}

