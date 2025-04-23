package com.pm.patientservice.kafka.events.producers;

import com.pm.patientservice.kafka.events.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreatedEventProducer extends BaseEvent {
    private String patientId;
    private String userId;
    private String firstName;
    private String lastName;
    private String gender;
}
