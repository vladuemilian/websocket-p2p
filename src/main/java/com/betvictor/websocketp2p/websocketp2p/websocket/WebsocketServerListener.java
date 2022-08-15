package com.betvictor.websocketp2p.websocketp2p.websocket;

import com.betvictor.websocketp2p.websocketp2p.dto.SendMessageRequest;
import com.betvictor.websocketp2p.websocketp2p.service.MessagingService;
import com.betvictor.websocketp2p.websocketp2p.service.UserService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketServerListener {

    private final SocketIOServer server;
    private final UserService userService;
    private final MessagingService messagingService;

    @Async
    public void start() throws InterruptedException {
        //the event that is used to associate the SessionID with the internal userToken
        server.addEventListener("register", Map.class, (client, data, ackRequest) -> {
            String senderToken = (String) data.get("senderToken");
            log.info("Got a register request for token: {} and sessionId: {}", senderToken, client.getSessionId());
            userService.createUserSession(client.getSessionId(), senderToken);
        });

        //the event that is used to broadcast the messages
        server.addEventListener("messaging", SendMessageRequest.class, (client, messageRequest, ackRequest) -> {
            log.info("Got a message: {}", messageRequest);
            messagingService.sendMessage(messageRequest);
        });

        server.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
