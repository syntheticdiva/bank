package com.java.bank.service;


import com.java.bank.dto.UserDTO;
import com.java.bank.entity.EmailData;
import com.java.bank.entity.PhoneData;
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

    @Transactional(readOnly = true)
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
                .map(user -> {
                    // Инициализация коллекций в рамках транзакции
                    user.getEmails().size();
                    user.getPhones().size();
                    return userMapper.toDto(user);
                });
    }

    @Transactional
    public void addEmail(Long userId, String newEmail) {
        User user = getUserById(userId);

        if (emailRepository.countByUser(user) >= 5) {
            throw new IllegalStateException("Maximum 5 emails per user");
        }

        if (emailRepository.existsByEmail(newEmail)) {
            throw new DuplicateEntityException("Email already registered");
        }

        EmailData email = new EmailData();
        email.setUser(user);
        email.setEmail(newEmail);
        email.setPrimary(user.getEmails().isEmpty()); // Первый email становится основным
        emailRepository.save(email);
    }
    @Transactional
    public void updateEmail(Long userId, String oldEmail, String newEmail) {
        if (oldEmail.equalsIgnoreCase(newEmail)) {
            throw new IllegalArgumentException("New email must be different");
        }

        // Загружаем пользователя с явной загрузкой emails
        User user = userRepository.findUserWithEmails(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        EmailData targetEmail = user.getEmails().stream()
                .filter(e -> e.getEmail().equals(oldEmail))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        if (emailRepository.existsByEmailAndOtherUser(newEmail, userId)) {
            throw new DuplicateEntityException("Email already taken");
        }

        targetEmail.setEmail(newEmail);
        // Не вызывайте emailRepository.save() — изменения сохранятся при коммите транзакции
    }

    @Transactional
    public void deleteEmail(Long userId, String email) {
        User user = userRepository.findByIdWithEmails(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getEmails().size() == 1) {
            throw new IllegalStateException("Cannot delete last email");
        }

        EmailData emailData = user.getEmails().stream()
                .filter(e -> e.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        if (emailData.isPrimary()) {
            throw new IllegalStateException("Cannot delete primary email");
        }

        // Удаляем через коллекцию пользователя с orphanRemoval
        user.getEmails().remove(emailData);
        userRepository.save(user); // Не обязательно, но гарантирует синхронизацию
    }

    @Transactional
    public void setPrimaryEmail(Long userId, String email) {
        User user = getUserById(userId);

        EmailData newPrimary = user.getEmails().stream()
                .filter(e -> e.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        user.getEmails().forEach(e -> e.setPrimary(false));
        newPrimary.setPrimary(true);
    }
    @Transactional
    public void addPhone(Long userId, String newPhone) {
        User user = getUserById(userId);

        if (phoneRepository.countByUser(user) >= 5) {
            throw new IllegalStateException("Maximum 5 phones per user");
        }

        if (phoneRepository.existsByPhone(newPhone)) {
            throw new DuplicateEntityException("Phone already registered");
        }

        PhoneData phone = new PhoneData();
        phone.setUser(user);
        phone.setPhone(newPhone);
        phone.setPrimary(user.getPhones().isEmpty()); // Первый телефон становится основным
        phoneRepository.save(phone);
    }
    @Transactional
    public void updatePhone(Long userId, String oldPhone, String newPhone) {
        if (oldPhone.equalsIgnoreCase(newPhone)) {
            throw new IllegalArgumentException("New phone must be different");
        }

        User user = getUserById(userId);

        PhoneData phoneData = user.getPhones().stream()
                .filter(p -> p.getPhone().equals(oldPhone))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Phone not found"));

        if (phoneRepository.existsByPhoneAndUserNot(newPhone, user)) {
            throw new DuplicateEntityException("Phone already taken");
        }

        phoneData.setPhone(newPhone);
    }
    @Transactional
    public void deletePhone(Long userId, String phone) {
        User user = getUserById(userId); // Загружаем с телефонами

        if (user.getPhones().size() == 1) {
            throw new IllegalStateException("Cannot delete last phone");
        }

        PhoneData phoneData = user.getPhones().stream()
                .filter(p -> p.getPhone().equals(phone))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Phone not found"));

        if (phoneData.isPrimary()) {
            throw new IllegalStateException("Cannot delete primary phone. Set new primary first.");
        }

        // Удаляем через коллекцию пользователя с orphanRemoval
        user.getPhones().remove(phoneData);
        userRepository.save(user); // Актуализируем состояние
    }
    @Transactional
    public void setPrimaryPhone(Long userId, String phone) {
        User user = getUserById(userId);

        PhoneData newPrimary = user.getPhones().stream()
                .filter(p -> p.getPhone().equals(phone))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Phone not found"));

        user.getPhones().forEach(p -> p.setPrimary(false));
        newPrimary.setPrimary(true);
    }



    //    private User getUserById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//    }
//private User getUserById(Long userId) {
//    return userRepository.findByIdWithEmails(userId)
//            .orElseThrow(() -> new EntityNotFoundException("User not found"));
//}
    @Transactional(readOnly = true)
    private User getUserById(Long userId) {
        return userRepository.findByIdWithEmailsAndPhones(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void validateEmailUniqueness(String email) {
        if (emailRepository.existsByEmail(email)) {
            throw new DuplicateEntityException("Email already registered");
        }
    }

}