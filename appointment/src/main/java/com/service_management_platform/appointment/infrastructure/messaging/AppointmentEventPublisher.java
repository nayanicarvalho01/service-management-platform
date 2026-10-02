package com.service_management_platform.appointment.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventPublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(AppointmentEventPublisher.class);

    private static final String TOPIC = "appointments.created";

    private final KafkaTemplate<String, AppointmentCreatedEvent> kafkaTemplate;

    public AppointmentEventPublisher(
            KafkaTemplate<String, AppointmentCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(AppointmentCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.appointmentId(), event)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        logger.error("Falha ao publicar atendimento no Kafka", error);
                    } else {
                        logger.info("Atendimento publicado no tópico {}", TOPIC);
                    }
                });
    }
}