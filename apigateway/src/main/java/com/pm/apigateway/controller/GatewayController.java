package com.pm.apigateway.controller;

import com.pm.apigateway.dto.PatientDetailsPayload;
import com.pm.apigateway.service.GatewayService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {
    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/patients/patient-details/{userId}")
    public Mono<PatientDetailsPayload> getPatientDetails(@PathVariable String userId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        // retrieve token from authorization header
        String token = authHeader.substring(7);
        return gatewayService.getPatientDetails(userId, token);
    }

    @GetMapping("/patients/all-details")
    public Flux<PatientDetailsPayload> getAllPatientWithDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        // retrieve token from authorization header
        String token = authHeader.substring(7);
        return gatewayService.getAllPatientsWithDetails(token);
    }
}
