package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.CallSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CallSessionSpringDataRepository extends JpaRepository<CallSessionJpaEntity, UUID> { }

