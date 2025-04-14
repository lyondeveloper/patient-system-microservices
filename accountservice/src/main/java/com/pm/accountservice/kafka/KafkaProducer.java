package com.pm.accountservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.accountservice.dto.user.UserResponseDTO;
import com.pm.accountservice.kafka.events.UserEvent;
import com.pm.accountservice.model.User;
import com.pm.accountservice.util.UserTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendUserCreatedEvent(User user) {
        UserEvent event = UserEvent.builder()
                .userId(user.getId().toString())
                .tenantId(user.getTenantId().toString())
                .userType(String.valueOf(UserTypes.fromName(String.valueOf(user.getType()))))
                .timestamp(user.getCreatedDate().toString())
                .message("User created successfully with the data attached")
                .build();
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("userCreatedSnapshot", payload);
        } catch(Exception e) {
            log.error("Error sending PatientCreated event: {}", e.getMessage());
        }
    }
}
