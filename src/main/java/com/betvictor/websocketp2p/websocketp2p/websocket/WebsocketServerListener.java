package com.betvictor.websocketp2p.websocketp2p.websocket;

import com.betvictor.websocketp2p.websocketp2p.dto.SendMessageRequest;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebsocketServer {

    @Value("${betvictor.websocket.hostname:0.0.0.0}")
    private String hostname;

    @Value("${betvictor.websocket.port:9000}")
    private Integer port;

    private final InMemoryWebsocketContext inMemoryWebsocketContext;

    @Async
    public void start() {
        Configuration config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);

        final SocketIOServer server = new SocketIOServer(config);

        //the event that is used to associated the SessionID with the internal userToken
        server.addEventListener("register", Object.class, new DataListener<Object>() {
                    @Override
                    public void onData(SocketIOClient client, Object data, AckRequest ackRequest) {
                        // broadcast messages to all clients
                        //server.getBroadcastOperations().sendEvent("chatevent", data);

                        client.getSessionId();
                    }
                });

        //the event that is used to broadcast the messages
        server.addEventListener("message", SendMessageRequest.class, new DataListener<SendMessageRequest>() {
            @Override
            public void onData(SocketIOClient client, SendMessageRequest messageRequest, AckRequest ackRequest) {
                //forwards the message to the receiver user
                inMemoryWebsocketContext.getSessionId(messageRequest.getReceiverUserToken())
                        .flatMap(senderSessionId -> server.getAllClients().stream()
                                .filter(registeredClient -> senderSessionId.equals(registeredClient.getSessionId()))
                                .findFirst())
                        .ifPresent(receiverClient -> receiverClient.sendEvent("message", "TODO: compute json object here"));
            }
        });

        server.start();

        while(true) {
            //do nothing, but keep this thread alive.
        }
    }
}
