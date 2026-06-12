package ru.tsu_taskgraph.core_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tsu_taskgraph.core_api.repository.UserRepository;
import ru.tsu_taskgraph.core_api.service.JwtProvider;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository; // Добавили репозиторий

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            var authHeader = request.getHeader("Authorization");

            // Изящная цепочка Optional:
            jwtProvider.getTokenFromAuthHeader(authHeader)    // 1. Достаем токен
                    .filter(jwtProvider::validateToken)       // 2. Проверяем валидность
                    .flatMap(jwtProvider::getUserIdFromToken) // 3. Извлекаем userId
                    .flatMap(userRepository::findById)        // 4. Достаем полноценного User из БД
                    .ifPresent(user -> {                      // 5. Если всё ок, авторизуем

                        var authToken = new UsernamePasswordAuthenticationToken(
                                user, // Кладем саму сущность как Principal!
                                null,
                                user.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    });

        } catch (Exception e) {
            log.error("Не удалось установить аутентификацию: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}