package com.match.adapter.persistence.adapter;

import com.match.adapter.persistence.jpa.UserJpaEntity;
import com.match.adapter.persistence.repository.UserSpringDataRepository;
import com.match.domain.port.out.UserRepositoryPort;
import com.match.domain.user.User;
import com.match.domain.user.UserStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserSpringDataRepository repo;

    public UserRepositoryAdapter(UserSpringDataRepository repo) { this.repo = repo; }

    @Override public User save(User u) {
        UserJpaEntity e = UserJpaEntity.builder()
            .id(u.id()).email(u.email()).passwordHash(u.passwordHash())
            .status(u.status().name()).createdAt(u.createdAt()).build();
        return toDomain(repo.save(e));
    }

    @Override public Optional<User> findById(UUID id) { return repo.findById(id).map(this::toDomain); }
    @Override public Optional<User> findByEmail(String email) { return repo.findByEmail(email).map(this::toDomain); }
    @Override public boolean existsByEmail(String email) { return repo.existsByEmail(email); }

    private User toDomain(UserJpaEntity e) {
        return new User(e.getId(), e.getEmail(), e.getPasswordHash(),
            UserStatus.valueOf(e.getStatus()), e.getCreatedAt());
    }
}

