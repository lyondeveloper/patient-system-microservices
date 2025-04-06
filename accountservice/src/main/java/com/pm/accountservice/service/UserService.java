package com.pm.accountservice.service;

import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.mapper.UserMapper;
import com.pm.accountservice.model.User;
import com.pm.accountservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserResponseDTO> getCurrentUserAuthenticated() {
        // cada vez que un usuario es loggeado, se guarda un JWT
        // pues este JWT cada vez que es guardado
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        var email = auth.getName();
        var userAuthenticated = userRepository.findByEmail(email);

        return userAuthenticated.map(UserMapper::toUserResponseDTO);
    }
}
