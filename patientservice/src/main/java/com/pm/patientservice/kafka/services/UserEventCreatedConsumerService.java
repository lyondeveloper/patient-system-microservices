package com.pm.patientservice.kafka.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.kafka.events.consumers.UserCreatedEvent;
import com.pm.patientservice.service.PatientService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventCreatedConsumerService {

    private final KafkaReceiver<String, String> receiver;
    private final ObjectMapper objMapper;
    private final PatientService patientService;

    @PostConstruct
    public void onStart() {
        log.info("Starting kafka consumer for UserCreated event...");

        // provee un Flux de ConsumerRecords (mensajes de kafka)
        // cada registro es procesado en el flujo uno por uno
        receiver.receive()
                .flatMap(record -> {
                    log.debug("Received potential userCreatedEvent message with key: {}", record.key());
                    // un callable de mono para obtener el evento reactivamente
                    return Mono.fromCallable(() -> objMapper.readValue(record.value(), UserCreatedEvent.class))
                            // se utiliza para ejecutar una acción secundaria o "de efecto colateral" mientras los datos fluyen por el `Mono` o `Flux`.
                            // Es útil para tareas como:
                            // Registrar información.
                            // Realizar métricas o validaciones.
                            // no se deberian mappear datos, ya que debemos pensar en el como un observador pasivo
                            // que hace actividades colaterales mientras la data fluye
                            // si se necesitan transformar datos
                            // se deben usar operadores como flatMap y eso.
                            .doOnNext(event -> log.info("UserCreated event received: {}", event))
                            // filtramos por userType
                            .filter(event -> Objects.equals(event.getUserType(), "USER_PATIENT"))
                            // luego otro flatMap para manejar la logica cuando obtengamos
                            // la data del evento
                            .flatMap(event -> {
                                // User is patient type, call reactive service
                                log.info("User is type patient, initiating patient creation process for userId: {}", event.getUserId());
                                var request = PatientRequestDTO.builder()
                                        .userId(event.getUserId())
                                        .firstName(event.getFirstName())
                                        .lastName(event.getLastName())
                                        .build();

                                return patientService.createPatient(request)
                                        .doOnNext(v -> log.info("Patient created successfully"))
                                        .doOnError(e -> log.error("Error creating patient: {}", e.getMessage()));
                            })
                            // Este `doOnError` maneja específicamente excepciones del tipo `JsonProcessingException`,
                            // que ocurren si hay un fallo en la deserialización del evento de Kafka.
                            .doOnError(JsonProcessingException.class, e -> {
                                log.error("Error deserializing event value [{}]: {}. Skipping record.", record.value(), e.getMessage());
                            })
                            // onErrorResume maneja errores genéricos, similar a un bloque `catch`. En este caso:
                            // Registra el error.
                            // Retorna un `Mono.empty()` para seguir con el siguiente mensaje sin interrumpir el flujo.
                            .onErrorResume(error -> {
                                log.error("Unexpected error processing record [key={}]: {}. Skipping record.", record.key(), error.getMessage(), error);
                                // retorna un vacio y ejecuta el siguiente evento
                                return Mono.empty();
                            })
                            // indica a kafka que se ha procesado correctamente el mensaje
                            // lo que asegura que el offset de este mensaje no sera procesado nuevamente
                            // y seguimos con otro mensaje
                            .then(Mono.fromRunnable(() -> record.receiverOffset().acknowledge()))
                            .doOnSuccess(v -> log.debug("Record offset committed for key {}", record.key()));
                })
                // error general de kafka, (ej: problema de conexion hacia kafka)
                .doOnError(e -> {
                    log.error("Error in kafka receving event", e.getMessage());
                })
                // podemos agregar retryWhen tambien? para que sirve y que logica deberia agregar?
                // subscribe es indispensable aca, hace que el flujo se subscriba en realidad y haga las operaciones
                .subscribe();

        log.info("Kafka consumer for UserCreated event started successfully");
    }
}
