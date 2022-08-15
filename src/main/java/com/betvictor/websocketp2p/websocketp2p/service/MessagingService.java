package com.betvictor.websocketp2p.websocketp2p.service;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.dto.SendMessageRequest;
import com.betvictor.websocketp2p.websocketp2p.repository.MessageRepository;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Optional;

@Service
public class MessagingService {

    private final MessageRepository messageRepository;
    private final JmsTemplate jmsTemplate;
    private final MessageDigest md;

    @SneakyThrows
    MessagingService(MessageRepository messageRepository, JmsTemplate jmsTemplate) {
        this.messageRepository = messageRepository;
        this.jmsTemplate = jmsTemplate;
        this.md = MessageDigest.getInstance("MD5");
    }

    public void sendMessage(SendMessageRequest sendMessageRequest) {
        sendMessageRequest.setCreatedAt(new Date());
        jmsTemplate.convertAndSend("messaging", sendMessageRequest);
    }

    /**
     * Idempotent operation of storing the message, due to the nature of the queueing mechanism which might retry messages,
     * we need to ensure that messages are not duplicated when they are stored.
     */
    public Message store(SendMessageRequest sendMessageRequest) {
        String messageHash = calculateMessageHash(sendMessageRequest);

        Optional<Message> existingMessage = messageRepository.findByHash(messageHash);

        if(existingMessage.isPresent()) {
            return existingMessage.get();
        }

        Message message = new Message();
        message.setBody(sendMessageRequest.getBody());
        message.setReceiverUserToken(sendMessageRequest.getReceiverUserToken());
        message.setSenderUserToken(sendMessageRequest.getSenderUserToken());
        message.setCreatedAt(sendMessageRequest.getCreatedAt());
        message.setHash(messageHash);

        return messageRepository.save(message);
    }

    private String calculateMessageHash(SendMessageRequest sendMessageRequest) {
        return DigestUtils.md5Hex(String.format("%s_%s", sendMessageRequest.getBody(), sendMessageRequest.getCreatedAt().toString()));
    }
}
