package com.betvictor.websocketp2p.websocketp2p.service;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.dto.SendMessageRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessagingEventHandlerTests {

    private final MessagingService messagingService = Mockito.mock(MessagingService.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final MessagingEventHandler cut = new MessagingEventHandler(messagingService, userService);

    @Test
    public void should_store_and_notify() {
        //GIVEN
        SendMessageRequest request = new SendMessageRequest();

        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .build();

        when(messagingService.store(any(SendMessageRequest.class)))
                .thenReturn(message);

        //WHEN
        cut.processMessage(request);

        //THEN
        verify(messagingService).store(request);
        verify(userService).notifyWithNewMessage(message);
    }
}