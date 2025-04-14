package com.pm.accountservice.kafka.events;

import com.pm.accountservice.util.UserTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String userId;
    private String userType;
    private String tenantId;
    private String message;
    private String timestamp;
}
