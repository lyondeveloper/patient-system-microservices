package com.pm.apigateway.dto;

public record PatientDetailsPayload(AccountDataPayload account, PatientDataPayload patient) {
}
