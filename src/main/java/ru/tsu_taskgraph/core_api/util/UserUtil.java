package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.AuthenticationException;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.UserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    /**
     * Получает пользователя {@link User} из SecurityContext.
     * Этот пользователь содержит только основные данные, которые были в токене,
     * и может быть неактуальным по сравнению с состоянием в БД.
     * @return {@link User} из контекста безопасности
     */
    public User getCurrentUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Пользователь не авторизован");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        throw new AuthenticationException("Principal имеет некорректный тип");
    }

    /**
     * Получает актуальное состояние пользователя из базы данных
     * на основе данных из SecurityContext.
     * @return {@link User} из базы данных
     */
    public User getCurrentUserFromDb() {
        User userFromContext = getCurrentUserFromContext();
        return getUserById(userFromContext.getId());
    }

    /**
     * Получает пользователя по его ID из базы данных.
     * @param id UUID пользователя
     * @return {@link User} из базы данных
     * @throws ResourceNotFoundException если пользователь не найден
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id=" + id + " не найден в базе данных"));
    }
}
