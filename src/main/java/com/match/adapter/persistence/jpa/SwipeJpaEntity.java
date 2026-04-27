package com.match.adapter.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "swipes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"swiper_id", "swipee_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SwipeJpaEntity {
    @Id private UUID id;
    @Column(name = "swiper_id", nullable = false) private UUID swiperId;
    @Column(name = "swipee_id", nullable = false) private UUID swipeeId;
    @Column(nullable = false) private String direction;
    @Column(nullable = false) private Instant createdAt;
}

