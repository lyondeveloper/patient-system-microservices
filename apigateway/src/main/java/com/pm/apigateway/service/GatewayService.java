package com.pm.apigateway.service;

import com.pm.apigateway.dto.AccountDataPayload;
import com.pm.apigateway.dto.PatientDataPayload;
import com.pm.apigateway.dto.PatientDetailsPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GatewayService {

    private static final Logger log = LoggerFactory.getLogger(GatewayService.class);
    private final WebClient accountsWebClient;
    private final WebClient patientWebClient;

    public GatewayService(@Qualifier("accountsWebClient") WebClient accountsWebClient,
                          @Qualifier("patientWebClient") WebClient patientWebClient) {
        this.accountsWebClient = accountsWebClient;
        this.patientWebClient = patientWebClient;
    }

    public Flux<PatientDetailsPayload> getAllPatientsWithDetails(String token) {
        // get all accounts by tenantId
        Flux<AccountDataPayload> accounts = accountsWebClient.get()
                .uri("/api/accounts/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(AccountDataPayload.class)
                .onErrorResume(Flux::error);

        // get all patients by tenantId
        Flux<PatientDataPayload> patients = patientWebClient.get()
                .uri("/api/patients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(PatientDataPayload.class)
                .onErrorResume(Flux::error);

        // combine each response into a list with correct DTO
        return Flux.zip(accounts, patients)
                .map(tuple ->
                        new PatientDetailsPayload(tuple.getT1(), tuple.getT2()));
    }

    public Mono<PatientDetailsPayload> getPatientDetails(String patientId, String jwtToken) {
        Mono<PatientDataPayload> patientData = patientWebClient.get()
                .uri("/api/patients/{patientId}", patientId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(PatientDataPayload.class)
                .onErrorResume(error -> {
                    log.error("Error while retrieving patient data for patientId: {}", patientId, error);
                    return Mono.error(error);
                });

        return patientData.flatMap(
                patientDataPayload -> {
                    String userId = String.valueOf(patientDataPayload.userId());
                    log.info("Retrieved patient data for patientId: {}, userId: {}", patientId, userId);
                    // stop entire operation if user doesnt exist
                    // we always want the patient with user data
                    if (userId == null) {
                        log.error("User not found for patientId: {}, skipping data fetching", patientId);
                        return Mono.error(new RuntimeException("User not found for patientId: " + patientId));
                    }

                    return accountsWebClient.get()
                            .uri("/api/accounts/users/{userId}", userId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                            .retrieve()
                            .bodyToMono(AccountDataPayload.class)
                            .map(account -> new PatientDetailsPayload(account, patientDataPayload))
                            .onErrorResume(error -> {
                                log.error("Error while retrieving user data for userId: {}", userId, error);
                                return Mono.error(error);
                            });
                }
        );
    }
}
