package com.java.bank.service;


import com.java.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    /**
     * Загружает пользователя по email для аутентификации.
     *
     * @param credential email || phone пользователя
     * @return объект {@link UserDetails} с данными пользователя
     * @throws UsernameNotFoundException если пользователь с указанным email не найден
     */
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//    }
    @Override
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        // Поиск по email или phone
        return userRepository.findByEmailOrPhone(credential)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + credential));
    }

    public UserDetails loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
}
