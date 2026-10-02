package com.service_management_platform.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentCreatedListener {

    private static final Logger logger =
            LoggerFactory.getLogger(AppointmentCreatedListener.class);

    @KafkaListener(topics = "appointments.created")
    public void receive(String message) {
        logger.info("Evento de atendimento recebido: {}", message);
    }
}