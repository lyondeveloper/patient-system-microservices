package com.pm.patientservice.config;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    // specify the kafka brokers directions
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ReceiverOptions<String, String> kafkaReceiverOptions() {
        Map<String, Object> props = new HashMap<>();

        // Setting up bootstrap servers and group id config
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // identify this consume with groupId
        String groupId = "patients";
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Set up deserializer for Strings
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // topic event wanted to consume
        String USER_CREATED_TOPIC_EVENT = "userCreatedSnapshot";
        return ReceiverOptions.<String, String>create(props)
                .subscription(Collections.singleton(USER_CREATED_TOPIC_EVENT));
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> kafkaReceiverOptions) {
        return KafkaReceiver.create(kafkaReceiverOptions);
    }
}
