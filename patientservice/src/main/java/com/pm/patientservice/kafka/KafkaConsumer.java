package com.pm.patientservice.kafka;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pm.patientservice.kafka.events.UserCreatedEvent;
//import com.pm.patientservice.service.PatientService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;

//@Service
public class KafkaConsumer {

//    private static final String USER_EVENT_CREATED_KAFKA_TOPIC = "userEventCreated";
//    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
//    private final PatientService patientService;
//
//    @KafkaListener(topics=USER_EVENT_CREATED_KAFKA_TOPIC, groupId="patients")
//    public void consumeEvent(String event) {
//        // handling possible parse error
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            UserCreatedEvent userCreatedEvent = objectMapper.readValue(event, UserCreatedEvent.class);
//            // if event received successfully, call service to insert
//            log.info("PatientCreated Event received: {}", userCreatedEvent);
//        } catch (Exception e) {
//            log.error("Error deserializaing event {}", e.getMessage());
//        }
//    }
}
