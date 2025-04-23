package com.pm.apigateway.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class PatientDetailsPayload {

    private final Map<String, Object> patientDetails;

    public PatientDetailsPayload(AccountDataPayload accountDataPayload, PatientDataPayload patientDataPayload) {
        this.patientDetails = new HashMap<>();
        this.patientDetails.putAll(convertToMap(accountDataPayload));
        this.patientDetails.putAll(convertToMap(patientDataPayload));
    }

    public Map<String, Object> getPatientDetails() {
        return this.patientDetails;
    }

    private Map<String, Object> convertToMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }
}
