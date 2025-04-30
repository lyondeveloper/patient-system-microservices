package com.pm.patientservice.kafka.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.kafka.events.consumers.UserCreatedEvent;
import com.pm.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventCreatedConsumerService {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final ObjectMapper objMapper;
    private Disposable subscription;
    private final PatientService patientService;

    private final String USER_CREATED_TOPIC = "userCreatedSnapshot";

    @EventListener(ApplicationStartedEvent.class)
    public void onStart() {
        log.info("Starting kafka consumer for UserCreated event...");

        this.subscription = kafkaReceiver.receive()
                .doOnNext(record -> log.info("Received new message: key={}, topic={}",
                        record.key(), record.topic()))
                                .flatMap(this::processUserEventCreated)
                                .subscribe();
    }

    private Mono<Void> processUserEventCreated(ReceiverRecord<String, String> record) {
        return Mono.fromCallable(() -> {
            log.info("Received user created event: Topic={}, Partition={}, Offset={}, Key={}, Value={}",
                    record.topic(), record.partition(), record.offset(), record.key(), record.value());

            try {
                var userEvent = objMapper.readValue(record.value(), UserCreatedEvent.class);
                log.info("UserCreated event received: {}", userEvent);
                return userEvent;
            } catch(JsonProcessingException e) {
                log.error("Error deserializing event value [{}]: {}. Skipping record.", record.value(), e.getMessage());
                throw e;
            }
        })
                .filter(event -> Objects.equals(event.getUserType(), "USER_PATIENT"))
                .flatMap(event -> {
                    log.info("User is type patient, initiating patient creation process for userId: {}", event.getUserId());
                    PatientRequestDTO request = PatientRequestDTO.builder()
                            .userId(event.getUserId())
                            .firstName(event.getFirstName())
                            .lastName(event.getLastName())
                            .build();

                    return patientService.createPatient(request)
                            .onErrorResume(e -> {
                                log.error("Error creating patient: {}", e.getMessage());
                                return Mono.error(new Exception("Error creating patient: " + e.getMessage()));
                            });
        })
                .doOnSuccess(unused -> record.receiverOffset().acknowledge())
                .doOnError(error -> log.error("Unexpected error processing record [key={}]: {}. Skipping record.", record.key(), error.getMessage(), error))
                .then();
    }

    public void stopConsumer() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}
