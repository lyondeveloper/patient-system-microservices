package com.pm.apigateway.service;

import com.pm.apigateway.dto.AccountDataPayload;
import com.pm.apigateway.dto.PatientDataPayload;
import com.pm.apigateway.dto.PatientDetailsPayload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GatewayService {

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

    public Mono<PatientDetailsPayload> getPatientDetails(String userId, String jwtToken) {

        // get patient and account data concurrently with Mono
        Mono<AccountDataPayload> accountData = accountsWebClient.get()
                .uri("/api/accounts/users/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(AccountDataPayload.class)
                .onErrorResume(Mono::error);

        Mono<PatientDataPayload> patientData = patientWebClient.get()
                .uri("/api/patients/byUserId/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(PatientDataPayload.class)
                .onErrorResume(Mono::error);

        // mono.zip is to combine async data together, returns a tuple
        return Mono.zip(accountData, patientData)
                .map(tuple ->
                        new PatientDetailsPayload(tuple.getT1(), tuple.getT2()));
    }
}
