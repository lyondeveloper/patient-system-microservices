package com.pm.accountservice.service;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.UserNotFound;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import com.pm.accountservice.kafka.KafkaProducer;
import com.pm.accountservice.mapper.UserMapper;
import com.pm.accountservice.model.User;
import com.pm.accountservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    public UserService(UserRepository userRepository,
                        KafkaProducer kafkaProducer
    ) {
        this.kafkaProducer = kafkaProducer;
        this.userRepository = userRepository;

    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserByEmailAndTenantId(String email, UUID tenantId) {
        return userRepository.findByEmailAndTenantId(email, tenantId);
    }

    public Optional<UserResponseDTO> getCurrentUserAuthenticated() {
        // cada vez que un usuario es loggeado, se guarda un JWT
        // pues este JWT cada vez que es guardado
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        var email = auth.getName();
        var tenantId = CommonService.getCurrentTenantId();
        var userAuthenticated = userRepository.findByEmailAndTenantId(email, UUID.fromString(tenantId));

        return userAuthenticated.map(UserMapper::toUserResponseDTO);
    }

    public UserResponseDTO getUserById(String userId) {
        var currentTenantId = CommonService.getCurrentTenantId();

        var userFound = userRepository.findByIdAndTenantIdWithRelations(UUID.fromString(userId), UUID.fromString(currentTenantId))
                .orElseThrow(() -> new UserNotFound("This user does not exist or does not belong to this tenant"));

        return UserMapper.toUserResponseDTO(userFound);
    }

    public List<UserResponseDTO> getAllUsersByTenantId() {
        var currentTenantId = CommonService.getCurrentTenantId();

        var users = userRepository.findAllWithRelationsByTenantId(UUID.fromString(currentTenantId));

        return users
                .stream()
                .map(UserMapper::toUserResponseDTO)
                .toList();
    }

    /**
     * method to create a user, call this from any method outside this UserService.
     *
     * @param userRequestDTO User DTO from client
     * @return user process successfully fully mapped with its tenant and address.
     * @throws UserProcessingException throw exception if doesnt work.
     */
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // check user if exists by email, throw error if true
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new CreateNewUserException("A user with this email already exists");
        }

        var currentTenantId = CommonService.getCurrentTenantId();

        userRequestDTO.setTenantId(UUID.fromString(currentTenantId));

        // no exceptions, all good, save
        var userCreated = userRepository.save(UserMapper.toUserModel(userRequestDTO));

        // send kafka event for any MS to catch the userCreated
        kafkaProducer.sendUserCreatedEvent(userCreated);

        return UserMapper.toUserResponseDTO(userCreated);
    }
}
