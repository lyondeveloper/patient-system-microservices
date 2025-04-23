package com.pm.accountservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.accountservice.kafka.events.UserEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class UserCreatedProducer {
    private final String USER_CREATED_TOPIC = "userCreatedSnapshot";
    private static final Logger log = LoggerFactory.getLogger(UserCreatedProducer.class);
    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    /**
     * Sends topic message that a user was created
     *
     * @param userEvent userEvent data to send.
     */
    public Mono<Void> send(UserEvent userEvent) {
        log.info("Starting kafka producer for topic: {}", USER_CREATED_TOPIC);
        try {
            var key = userEvent.getUserId();
            // convert userEvent to JSON
            String value = objectMapper.writeValueAsString(userEvent);

            // Create correct message for kafka
            SenderRecord<String, String, String> record = SenderRecord.create(
                    USER_CREATED_TOPIC,
                    null,
                    System.currentTimeMillis(),
                    key,
                    value,
                    key
            );

            // Mono.just convierte el mensaje creado a un flujo reactivo
            // requerido porque Kafka trabaja con flujos Publisher
            // Caracteristicas del mono.just
            // 1. Emite un único valor y finaliza:
            //    - Una vez que emite el valor (tu `SenderRecord`), el flujo se completa.
            //
            // 2. Sincrónico en la creación, asíncrono en el procesamiento:**
            //    - El flujo no se ejecuta hasta que alguien lo "suscriba" (en este caso, el `kafkaSender`).
            //
            // 3. Formato estándar para programación reactiva:**
            //    - Convierte un objeto regular (`record`) en un flujo reactivo (`Mono`) que cualquier operador o método reactivo puede interpretar.
            return kafkaSender.send(Mono.just(record))
                    .doOnNext(result -> {
                        log.info("Topic message send successfully: {}", result.recordMetadata());
                    })
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
