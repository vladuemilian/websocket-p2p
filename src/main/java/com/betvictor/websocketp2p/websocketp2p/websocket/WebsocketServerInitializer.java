package com.betvictor.websocketp2p.websocketp2p.websocket;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketServerInitializer {

    private final WebsocketServerListener websocketServer;

    //does nothing, but calls the websocketServer.start(); to start a new thread with the ws server.
    //had to be on a separated class / bean due to the nature of @Async, which uses java proxies under the hood.
    @PostConstruct
    public void initialize() throws Exception {
        log.info("Starting the webserver.");
        websocketServer.start();
        log.info("Successfully started the webserver.");
    }
}
