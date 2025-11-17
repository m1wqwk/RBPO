package com.example.laboratornie.service;

import com.example.laboratornie.model.User;
import com.example.laboratornie.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Регулярное выражение для проверки пароля
    // Минимум 8 символов, хотя бы одна цифра и один спецсимвол
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для регистрации нового пользователя
    public User registerUser(String username, String email, String password, String role) {
        // Проверяем, не существует ли уже пользователь с таким именем
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Имя пользователя уже существует.");
        }

        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже существует.");
        }

        // Проверяем надежность пароля
        if (!isPasswordValid(password)) {
            throw new IllegalArgumentException(
                    "Пароль не соответствует требованиям безопасности. " +
                            "Пароль должен содержать не менее 8 символов,  не менее одной цифры и специальный символ."
            );
        }

        // Создаем нового пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Хэшируем пароль
        user.setRole(role != null ? role : "USER"); // По умолчанию роль USER
        user.setEnabled(true);

        return userRepository.save(user);
    }

    // Метод для проверки надежности пароля
    private boolean isPasswordValid(String password) {
        return pattern.matcher(password).matches();
    }
}