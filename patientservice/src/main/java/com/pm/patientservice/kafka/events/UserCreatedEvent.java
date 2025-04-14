package com.pm.patientservice.kafka.events;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String userId;
    private String userType;
    private String tenantId;
    private String message;
    private String timestamp;
}
