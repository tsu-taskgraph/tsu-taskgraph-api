package ru.tsu_taskgraph.core_api.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.ProjectStatus;

import java.util.UUID;

public class ProjectSpecification {

    public static Specification<Project> userIsMemberOrOwner(UUID userId) {
        return (root, query, criteriaBuilder) -> {
            // Ensure distinct results
            query.distinct(true);
            // Join with members
            var memberJoin = root.join("members", jakarta.persistence.criteria.JoinType.LEFT);
            // Create predicates
            var ownerPredicate = criteriaBuilder.equal(root.get("owner").get("id"), userId);
            var memberPredicate = criteriaBuilder.equal(memberJoin.get("user").get("id"), userId);
            return criteriaBuilder.or(ownerPredicate, memberPredicate);
        };
    }

    public static Specification<Project> statusEquals(ProjectStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Project> nameContains(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
