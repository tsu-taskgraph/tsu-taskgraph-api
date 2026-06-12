package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsu_taskgraph.core_api.entiry.RefreshToken;
import ru.tsu_taskgraph.core_api.entiry.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}