package com.pm.patientservice.kafka.events.consumers;

import com.pm.patientservice.kafka.events.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent extends BaseEvent {
    private Long userId;
    private Long tenantId;
    private String userType;
    private String firstName;
    private String lastName;
}
