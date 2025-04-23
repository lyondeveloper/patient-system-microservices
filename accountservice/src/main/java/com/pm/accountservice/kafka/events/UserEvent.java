package com.pm.accountservice.kafka.events;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String userId;
    private String userType;
    private String tenantId;
    private String firstName;
    private String lastName;
    private String message;
}
