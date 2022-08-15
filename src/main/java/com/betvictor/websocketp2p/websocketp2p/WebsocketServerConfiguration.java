package com.betvictor.websocketp2p.websocketp2p;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebsocketServerConfiguration {

    @Value("${betvictor.websocket.hostname:0.0.0.0}")
    private String hostname;

    @Value("${betvictor.websocket.port:9000}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        config.setRandomSession(true);

        return new SocketIOServer(config);
    }
}
