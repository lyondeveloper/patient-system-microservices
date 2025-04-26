package com.pm.accountservice.service;

import com.pm.accountservice.dto.address.AddressResponseDTO;
import com.pm.accountservice.dto.user.UserRequestDTO;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.exceptions.address.AddressExceptions;
import com.pm.accountservice.exceptions.tenants.TenantNotFoundException;
import com.pm.accountservice.exceptions.users.CreateNewUserException;
import com.pm.accountservice.exceptions.users.UserNotFound;
import com.pm.accountservice.exceptions.users.UserProcessingException;
import com.pm.accountservice.kafka.UserCreatedProducer;
import com.pm.accountservice.kafka.events.UserEvent;
import com.pm.accountservice.mapper.UserMapper;
import com.pm.accountservice.model.Tenant;
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
    private final AddressService addressService;

    public UserService(UserRepository userRepository,
                        UserCreatedProducer userCreatedProducer,
                       CommonService commonService,
                       AddressService addressService
    ) {
        this.userCreatedProducer = userCreatedProducer;
        this.userRepository = userRepository;
        this.commonService = commonService;
        this.addressService = addressService;
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

                    return commonService.getCurrentTenantId()
                            .flatMap(tenantId -> findUserByEmailAndTenantId(userEmail, tenantId)
                                    .switchIfEmpty(Mono.error(() -> new UserNotFound("User not found")))
                                    .doOnError((ex) -> log.error("User not found with email {} and tenantId {}", userEmail, tenantId))
                                    .map(UserMapper::toUserResponseDTO)
                            .switchIfEmpty(Mono.error(() -> new TenantNotFoundException("This tenant doesnt exist")))
                            .doOnError((ex) -> log.error("Error on retrieving tenantId: {}", ex.getMessage())));
                })
                .switchIfEmpty(Mono.error(new Exception("Security context not found")));
    }

   public Mono<UserResponseDTO> getUserById(String id) {
        // using mono.fromCallable even though we get id from path
       // because we need to match async operations with getCurrentTenantId that is a mono call
        var userIdMono = Mono.fromCallable(() -> {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new UserNotFound("Invalid userId");
            }
        });

        // we use tuples to transport multiple values throughout our reactive flow
        // and to preserve the context inside the operators
        // to combine different results from different reactive sources

       // zipWith is a project reactor operator to combine elements emitted by different reactive sources
       // for example here we combine the above transformed Mono called to retrieve userId and the
       // reactive return of getCurrentTenantId
       // we use it when we need to process 2 or more reactive flows together
       // to wait until both of them are sucessfully completed before returning
       // to fusion relational data into one and process data or run logic

       // Mono.zip se usa para 2 o mas fuentes asincronas
       // podemos usar zipWith para solamente 2
        return userIdMono.zipWith(commonService.getCurrentTenantId())
                .flatMap(tuple -> {
                    var userId = tuple.getT1();
                    var tenantId = tuple.getT2();

                    log.info("Getting user by id {} and tenantId {}", userId, tenantId);

                    return userRepository.findByIdAndTenantIdWithRelations(userId, tenantId)
                            .onErrorResume(ex -> Mono.error(new UserNotFound("User not found")))
                            .doOnError((ex) -> log.error("User not found with id {} and tenantId {}", userId, tenantId))
                            .flatMap(this::associateUserWithAddress);
                })
                .onErrorResume(ex -> {
                    log.error("Error on retrieving user: {}", ex.getMessage());

                    if (ex instanceof TenantNotFoundException) {
                        return Mono.error(new TenantNotFoundException("This tenant doesnt exist"));
                    } else if (ex instanceof UserNotFound) {
                        return Mono.error(new UserNotFound("User not found"));
                    }

                    return Mono.error(new UserProcessingException("Error while retrieving user"));
                });
    }

    public Flux<UserResponseDTO> findAllUsersByTenantId() {
        return commonService.getCurrentTenantId()
                // flatMapMany es necesario para combinar el resultado Mono de getCurrenTenantId
                // y el resultado Flux de findAll
                .flatMapMany(tenantId -> userRepository
                        .findAllWithRelationsByTenantId(tenantId)
                        .flatMap(this::associateUserWithAddress))
                        .onErrorResume((ex) -> {
                            log.error("Error on retrieving users: {}", ex.getMessage());
                            return Flux.error(new UserNotFound("Non user found, unexpected error"));
                        })
                .onErrorResume(ex -> {
                    log.error("Error obtaining tenantId: {}", ex.getMessage());
                    return Flux.error(new TenantNotFoundException("Tenant could not be retrieved: " + ex.getMessage()));
                });
    }

    private Mono<UserResponseDTO> associateUserWithAddress(User user) {
        if (user.getAddressId() != null) {
            return addressService.getAddressById(user.getAddressId())
                    .map(address -> {
                        UserResponseDTO userResponseDto = UserMapper.toUserResponseDTO(user);
                        userResponseDto.setAddress(address);
                        return userResponseDto;
                    })
                    .doOnError((ex) -> log.error("Address not found: {}", ex.getMessage()))
                    .onErrorResume(ex -> Mono.error(new AddressExceptions("Address not found")));
        }

        return Mono.just(UserMapper.toUserResponseDTO(user));
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
