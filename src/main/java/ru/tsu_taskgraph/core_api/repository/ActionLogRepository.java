package ru.tsu_taskgraph.core_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tsu_taskgraph.core_api.entity.ActionLogEntry;

import java.util.UUID;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLogEntry, UUID>, JpaSpecificationExecutor<ActionLogEntry> {
}
