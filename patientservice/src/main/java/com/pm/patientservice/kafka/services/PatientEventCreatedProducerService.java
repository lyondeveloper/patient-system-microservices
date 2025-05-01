package com.pm.patientservice.kafka.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.kafka.events.producers.PatientCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientEventCreatedProducerService {

    private final String PATIENT_CREATED_TOPIC = "patientCreatedSnapshot";
    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    public Mono<Void> send(PatientCreatedEvent patientEvent) {
        log.info("Starting kafka producer for topic: {}", PATIENT_CREATED_TOPIC);
        try {
            var key = patientEvent.getId();
            // convert event to JSON
            String value = objectMapper.writeValueAsString(patientEvent);

            // Create correct message for kafka
            SenderRecord<String, String, String> record = SenderRecord.create(
                    PATIENT_CREATED_TOPIC,
                    null,
                    System.currentTimeMillis(),
                    key,
                    value,
                    key
            );

            return kafkaSender.send(Mono.just(record))
                    .doOnNext(result -> log.info("Topic message send successfully: {}", result.recordMetadata()))
                    .doOnError(e -> log.error("Error sending topic message: {}", e.getMessage()))
                    .then();
        } catch(JsonProcessingException e) {
            log.error("Error serializing userEvent: {}", e.getMessage());
            return Mono.error(e);
        } catch(Exception e) {
            log.error("Unexpected error sending userEvent: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}
