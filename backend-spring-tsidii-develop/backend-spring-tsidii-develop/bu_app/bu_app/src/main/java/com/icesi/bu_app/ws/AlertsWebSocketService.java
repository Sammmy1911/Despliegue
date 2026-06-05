package com.icesi.bu_app.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.controller.rest.dto.AlertResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertsWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(AlertResponse alert) {
        messagingTemplate.convertAndSend("/topic/alerts", alert);
    }
}
