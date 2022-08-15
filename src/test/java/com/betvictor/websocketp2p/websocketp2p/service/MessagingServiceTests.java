package com.betvictor.websocketp2p.websocketp2p.service;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.dto.SendMessageRequest;
import com.betvictor.websocketp2p.websocketp2p.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;


import java.util.Date;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessagingServiceTests {

    private final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    private final JmsTemplate jmsTemplate = Mockito.mock(JmsTemplate.class);

    private final MessagingService messagingService = new MessagingService(messageRepository, jmsTemplate);

    @Test
    public void should_send_message() {
        //GIVEN
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setReceiverUserToken("receiver");

        //WHE
        messagingService.sendMessage(sendMessageRequest);

        //THEN
        ArgumentCaptor<SendMessageRequest> sendMessageRequestArgumentCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);

        verify(jmsTemplate).convertAndSend(eq("messaging"), sendMessageRequestArgumentCaptor.capture());
        SendMessageRequest sentMessageRequest = sendMessageRequestArgumentCaptor.getValue();
        assertThat(sentMessageRequest.getCreatedAt()).isNotNull();
        assertThat(sentMessageRequest.getReceiverUserToken()).isEqualTo(sendMessageRequest.getReceiverUserToken());
    }

    @Test
    public void should_skip_storing_for_an_existing_message_for_same_hash() {
        //GIVEN
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setBody("body");
        sendMessageRequest.setCreatedAt(new Date());

        when(messageRepository.findByHash(any())).thenReturn(Optional.of(new Message()));

        //WHEN
        messagingService.store(sendMessageRequest);

        //THEN
        verify(messageRepository, times(0)).save(any(Message.class));
    }

    @Test
    public void should_store_for_new_message() {
        //GIVEN
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setReceiverUserToken("receiver");
        sendMessageRequest.setSenderUserToken("sender");
        sendMessageRequest.setBody("body");
        sendMessageRequest.setCreatedAt(new Date());

        when(messageRepository.findByHash(any())).thenReturn(Optional.empty());

        //WHEN
        messagingService.store(sendMessageRequest);

        //THEN
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

        verify(messageRepository).save(messageArgumentCaptor.capture());

        Message saved = messageArgumentCaptor.getValue();
        assertThat(saved.getBody()).isEqualTo(sendMessageRequest.getBody());
        assertThat(saved.getReceiverUserToken()).isEqualTo(sendMessageRequest.getReceiverUserToken());
        assertThat(saved.getSenderUserToken()).isEqualTo(sendMessageRequest.getSenderUserToken());
        assertThat(saved.getCreatedAt()).isEqualTo(sendMessageRequest.getCreatedAt());
        assertThat(saved.getHash()).isNotBlank();
    }

}