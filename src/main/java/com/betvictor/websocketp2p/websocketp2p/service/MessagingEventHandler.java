package com.betvictor.websocketp2p.websocketp2p.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessagingRoutingService {

    /**
     * Main queue that ensures the routing of the messages to the desired
     * @param content
     */
    @JmsListener(destination = "messaging")
    public void processMessage(String content) {

    }

}
