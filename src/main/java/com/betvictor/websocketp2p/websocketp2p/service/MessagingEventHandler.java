package com.betvictor.websocketp2p.websocketp2p.service;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessagingEventHandler {

    private final MessagingService messagingService;
    private final UserService userService;

    /**
     * Main queue that ensures the routing of the messages to the desired
     * @param request
     */
    @JmsListener(destination = "messaging")
    public void processMessage(SendMessageRequest request) {
        log.info("Start message processing for senderToken: {} to user: {}", request.getSenderUserToken(), request.getReceiverUserToken());
        Message message = messagingService.store(request);

        int notifiedClientsCount = userService.notifyWithNewMessage(message);
        log.info("Successfully notified: {} clients.", notifiedClientsCount);
    }

}
