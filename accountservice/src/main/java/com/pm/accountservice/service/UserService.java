package com.pm.accountservice.service;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import com.pm.accountservice.mapper.UserMapper;
import com.pm.accountservice.model.Tenant;
import com.pm.accountservice.model.User;
import com.pm.accountservice.repository.TenantRepository;
import com.pm.accountservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

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

    /**
     * Main method to process a user and associate them with its tenants.
     * This is not a transactional method but calls methods that are
     * capturing exceptions along the way.
     *
     * @param newUser User to process
     * @throws UserProcessingException throw exception if doesnt work.
     */
//    private void processNewUserAssociationWithTenants(User newUser) throws UserProcessingException {
//        try {
//            log.info("Processing new user association");
//            associateUserWithItsTenants(newUser);
//            log.info("New user association processed");
//        } catch (DataAccessException e) {
//            // catching specific exception on data saving or association
////            log.warn("Data error in DB while associating user: {}", e.getMessage());
//            throw new UserProcessingException("Error in DB while associating user: " + e);
//        } catch (Exception e) {
////            log.warn("Unexpected error while associating user: {}", e.getMessage());
//            throw new UserProcessingException("Unexpected error while associating user: " + e);
//        }
//    }
    /**
     * Transactional method to create a user, call this from any method outside this UserService.
     *
     * @param userRequestDTO User DTO from client
     * @return user process successfully fully mapped with its tenants.
     * @throws UserProcessingException throw exception if doesnt work.
     */
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // check user if exists by email, throw error if true
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new CreateNewUserException("A user with this email already exists");
        }

        // no exceptions, all good, save
        var userCreated = userRepository.save(UserMapper.toUserModel(userRequestDTO));
//TODO: luego veo que pasa con la asociacion
//        if (userRequestDTO.getTenantsIds() != null && !userRequestDTO.getTenantsIds().isEmpty()) {
//            log.info("Started association");
//            // associating tenants with the userCreated
//            processNewUserAssociationWithTenants(userCreated);
//        } else {
//            log.info("No tenants to associate with this user {}", userCreated.getId());
//        }

        return UserMapper.toUserResponseDTO(userCreated);
    }
}
