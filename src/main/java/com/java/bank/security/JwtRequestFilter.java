package com.java.bank.security;

import com.java.bank.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для обработки JWT-токенов во входящих запросах.
 * <p>
 * Наследует {@link OncePerRequestFilter} для однократной обработки каждого запроса.
 * Выполняет следующие задачи:
 * <ol>
 *   <li>Извлекает JWT из заголовка Authorization</li>
 *   <li>Проверяет валидность токена (срок действия, подпись)</li>
 *   <li>Загружает данные пользователя при успешной проверке</li>
 *   <li>Устанавливает аутентификацию в контекст безопасности</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Основной метод обработки запроса.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param chain цепочка фильтров
     * @throws ServletException при ошибках сервлета
     * @throws IOException при ошибках ввода/вывода
     *
     * @implSpec Логика работы:
     * 1. Проверка заголовка Authorization
     * 2. Извлечение и валидация JWT
     * 3. Обработка исключений:
     *    - 401: Истекший токен
     *    - 403: Неверная подпись
     *    - 400: Прочие ошибки валидации
     * 4. Установка аутентификации в SecurityContext
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        try {
            String token = extractToken(request);
            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            response.sendError(401, "JWT expired");
            return;
        } catch (Exception ex) {
            response.sendError(400, "Invalid token");
            return;
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
