package com.pm.accountservice.service;

import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.exceptions.tenants.TenantNotFoundException;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.UserNotFound;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import com.pm.accountservice.kafka.UserCreatedProducer;
import com.pm.accountservice.kafka.events.UserEvent;
import com.pm.accountservice.mapper.UserMapper;
import com.pm.accountservice.model.User;
import com.pm.accountservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserCreatedProducer userCreatedProducer;
    private final CommonService commonService;

    public UserService(UserRepository userRepository,
                        UserCreatedProducer userCreatedProducer,
                       CommonService commonService
    ) {
        this.userCreatedProducer = userCreatedProducer;
        this.userRepository = userRepository;
        this.commonService = commonService;
    }

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> findUserByEmailAndTenantId(String email, Long tenantId) {
        log.info("Finding user by email {} and tenantId {}", email, tenantId);
        return userRepository.findByEmailAndTenantId(email, tenantId)
                .doOnSuccess(user -> log.info("UserService: User found: {}", user))
                .doOnError(error -> log.error("UserService: User not found: {}", error.getMessage()));
    }

    public Mono<UserResponseDTO> getCurrentUserAuthenticated() {
        // getting authenticated user from SecurityContext to return
        // authenticated user data
        return ReactiveSecurityContextHolder.getContext()
                .flatMap((securityContext) -> {
                    var auth = securityContext.getAuthentication();

                    if (auth == null || !auth.isAuthenticated()) {
                        return Mono.error(new UserNotFound("User not authenticated"));
                    }

                    var userEmail = auth.getName();

                    // chaining down getcurrenttenant id
                    // if successfull call findUserByEmailAndTenantId and chaining with correspondant error handling
                    return commonService.getCurrentTenantId()
                            .flatMap(tenantId -> findUserByEmailAndTenantId(userEmail, Long.valueOf(tenantId))
                                    .switchIfEmpty(Mono.error(() -> new UserNotFound("User not found")))
                                    .doOnError((ex) -> log.error("User not found with email {} and tenantId {}", userEmail, tenantId))
                                    .map(UserMapper::toUserResponseDTO)
                            .switchIfEmpty(Mono.error(() -> new TenantNotFoundException("This tenant doesnt exist")))
                            .doOnError((ex) -> log.error("Error on retrieving tenantId: {}", ex.getMessage())));
                })
                .switchIfEmpty(Mono.error(new Exception("Security context not found")));
    }

   public Mono<UserResponseDTO> getUserById(String userId) {
        return commonService.getCurrentTenantId()
                .flatMap(tenantId -> userRepository.findByIdAndTenantIdWithRelations(Long.valueOf(userId), Long.valueOf(tenantId))
                        .switchIfEmpty(Mono.error(() -> new UserNotFound("User not found")))
                        .doOnError((ex) -> log.error("User not found with id {} and tenantId {}", userId, tenantId))
                        .map(UserMapper::toUserResponseDTO))
                .onErrorResume(ex -> Mono.error(new TenantNotFoundException("Tenant not found")))
                .doOnError((ex) -> log.error("Error on retrieving tenantId: {}", ex.getMessage()));
    }

    public Flux<UserResponseDTO> findAllUsersByTenantId() {
        return commonService.getCurrentTenantId()
                // flatMapMany es necesario para combinar el resultado Mono de getCurrenTenantId
                // y el resultado Flux de findAll
                .flatMapMany(tenantId -> userRepository.findAllWithRelationsByTenantId(Long.valueOf(tenantId))
                        .map(UserMapper::toUserResponseDTO))
                        .onErrorResume((ex) -> {
                            log.error("Error on retrieving users: {}", ex.getMessage());
                            return Flux.error(new UserNotFound("Non user found, unexpected error"));
                        })
                .onErrorResume(ex -> {
                    log.error("Error obtaining tenantId: {}", ex.getMessage());
                    return Flux.error(new TenantNotFoundException("Tenant could not be retrieved: " + ex.getMessage()));
                });
    }

    /**
     * method to create a user, call this from any method outside this UserService.
     *
     * @param userRequestDTO User DTO from client
     * @return user process successfully fully mapped with its tenant and address.
     * @throws UserProcessingException throw exception if doesnt work.
     */
    public Mono<UserResponseDTO> createUser(UserRequestDTO userRequestDTO) {
        return commonService
                .getCurrentTenantId()
                .flatMap((tenantId) -> userRepository
                        .existsByEmailAndTenantId(userRequestDTO.getEmail(), tenantId)
                        .flatMap((exists) -> {
                            if (exists) {
                                log.error("A user with this email already exists");
                                return Mono.error(new CreateNewUserException("A user with this email already exists"));
                            }
                            userRequestDTO.setTenantId((tenantId));

                            log.info("Creating user: {}", userRequestDTO);

                            var newUser = UserMapper.toUserModel(userRequestDTO);

                            newUser.setNew(true);

                            return userRepository.save(newUser)
                                    .flatMap((userCreated) -> {
                                        log.info("User created successfully, sending kafka message: {}", userCreated);
                                        userCreated.markNotNew();
                                        // mapping user and returning ResponseDTO
                                        // discarding kafka message with .thenReturn
                                        var mappedUserCreated = UserMapper.toUserResponseDTO(userCreated);
                                        return userCreatedProducer.send(UserEvent.builder()
                                                        .userId(String.valueOf(userCreated.getId()))
                                                        .userType(userCreated.getType().toString())
                                                        .message("User created successfully with attached data")
                                                        .firstName(userCreated.getFirstName())
                                                        .lastName(userCreated.getLastName())
                                                        .tenantId(userCreated.getTenantId().toString())
                                                        .build())
                                                //.thenReturn hace que se descarte el return de arriba de retornar la llamada
                                                // a kafka, para retornar nuestro DTO y que el producer haga su logica reactivamente
                                                .thenReturn(mappedUserCreated);
                                    })
                                    .onErrorResume((ex) -> {
                                        log.error("Error while saving: {}", ex.getMessage());
                                        return Mono.error(new UserProcessingException("Unexpected error while saving user"));
                                    });
                        })
                        // metodo que permite transformar cualquier excepcion de bajo nivel (como DataAccess) a una excepcion personalizada
                        .onErrorMap(DataAccessException.class, ex -> {
                            log.error("DB error: {}", ex.getMessage());
                            return new UserProcessingException("DB error: " + ex.getMessage());
                        })
                        .onErrorMap(RuntimeException.class, ex -> {
                            log.error("Unexpected runtime error: {}", ex.getMessage());
                            return new UserProcessingException("Unexpected runtime error: " + ex.getMessage());
                        })
                        // util para debugging
                        .doOnError(error -> log.error("Unexpected server error: {}", error.getMessage())))
                .onErrorResume((ex) -> {
                    log.error("Error obtaining tenantId: {}", ex.getMessage());
                    return Mono.error(new TenantNotFoundException("Tenant could not be retrieved: " + ex.getMessage()));
                });
    }
}
