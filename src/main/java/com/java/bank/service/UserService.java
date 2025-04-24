package com.java.bank.service;


import com.java.bank.dto.UserDTO;
import com.java.bank.entity.EmailData;
import com.java.bank.entity.User;
import com.java.bank.exception.DuplicateEntityException;
import com.java.bank.exception.EntityNotFoundException;
import com.java.bank.mapper.UserMapper;
import com.java.bank.repository.AccountRepository;
import com.java.bank.repository.EmailRepository;
import com.java.bank.repository.PhoneRepository;
import com.java.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;
    private final AccountRepository accountRepository;
    private final UserMapper userMapper;

    public Page<UserDTO> searchUsers(String name, LocalDate dateOfBirth,
                                     String email, String phone, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(UserSpecifications.hasNameStartingWith(name));
        }
        if (dateOfBirth != null) {
            spec = spec.and(UserSpecifications.bornAfter(dateOfBirth));
        }
        if (email != null) {
            spec = spec.and(UserSpecifications.hasEmail(email));
        }
        if (phone != null) {
            spec = spec.and(UserSpecifications.hasPhone(phone));
        }

        return userRepository.findAll(spec, pageable)
                .map(userMapper::toDto);
    }


//    @Transactional
//    public void addEmail(Long userId, String email) {
//        validateEmailUniqueness(email);
//        User user = getUserById(userId);
//
//        if (emailRepository.countByUser(user) >= 5) {
//            throw new IllegalStateException("Maximum 5 emails per user");
//        }
//
//        emailRepository.save(new EmailData(user, email));
//    }

    @Transactional
    public void updateEmail(Long userId, String oldEmail, String newEmail) {
        validateEmailUniqueness(newEmail);
        User user = getUserById(userId);

        EmailData emailData = emailRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        if (!emailData.getUser().equals(user)) {
            throw new SecurityException("Email doesn't belong to user");
        }

        if (emailRepository.countByUser(user) == 1) {
            throw new IllegalStateException("Cannot delete last email");
        }

        emailData.setEmail(newEmail);
        emailRepository.save(emailData);
    }

    // Аналогичные методы для телефонов

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void validateEmailUniqueness(String email) {
        if (emailRepository.existsByEmail(email)) {
            throw new DuplicateEntityException("Email already registered");
        }
    }
}