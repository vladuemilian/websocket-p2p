package com.betvictor.websocketp2p.websocketp2p;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableMongoAuditing //required for createdAt to be automatically populated when saving the entity
@SpringBootApplication
@EnableMongoRepositories
@EnableAsync //required in order to process the @Async operation, when the websocket server is launched
@EnableJms
public class WebsocketP2pApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketP2pApplication.class, args);
	}

}
