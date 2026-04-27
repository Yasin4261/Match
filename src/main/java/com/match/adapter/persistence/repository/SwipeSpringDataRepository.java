package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.SwipeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SwipeSpringDataRepository extends JpaRepository<SwipeJpaEntity, UUID> {
    Optional<SwipeJpaEntity> findBySwiperIdAndSwipeeId(UUID swiperId, UUID swipeeId);
    boolean existsBySwiperIdAndSwipeeIdAndDirection(UUID swiperId, UUID swipeeId, String direction);
}

