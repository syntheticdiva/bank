package com.java.bank.service;


import com.java.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис для загрузки пользовательских данных в рамках Spring Security.
 * <p>
 * Реализует интерфейс {@link UserDetailsService}, предоставляя механизм аутентификации
 * через email пользователя. Интегрируется с системой безопасности Spring для управления доступом.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#credential")
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        return userRepository.findByEmailOrPhone(credential)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + credential));
    }

    @Cacheable(value = "users", key = "'id:' + #userId")
    public UserDetails loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
}
