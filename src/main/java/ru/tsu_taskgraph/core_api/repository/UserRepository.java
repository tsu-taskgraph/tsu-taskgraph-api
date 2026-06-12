package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsu_taskgraph.core_api.entiry.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}