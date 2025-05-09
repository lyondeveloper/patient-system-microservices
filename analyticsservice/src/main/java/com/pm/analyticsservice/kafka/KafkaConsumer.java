package com.pm.patientservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics="patient", groupId="analyticsms")
    public void consumeEvent(byte[] event) {
        // handling possible parse error
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("PatientCreated Event received: {}", patientEvent.toString());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializaing event {}", e.getMessage());
        }
    }
}
