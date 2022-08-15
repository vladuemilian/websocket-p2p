package com.betvictor.websocketp2p.websocketp2p.repository;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Date;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest //starts an in memory database
public class MessageRepositoryTests {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void should_find_messages() {
        //GIVEN
        String sender1 = UUID.randomUUID().toString();
        String receiver1 = UUID.randomUUID().toString();

        Message message1 = given_message(sender1, receiver1);

        String sender2 = UUID.randomUUID().toString();
        String receiver2 = UUID.randomUUID().toString();

        Message message2 = given_message(sender2, receiver2);

        String sender3 = UUID.randomUUID().toString();
        String receiver3 = UUID.randomUUID().toString();

        Message message3 = given_message(sender3, receiver3);

        messageRepository.saveAll(List.of(message1, message2, message3));

        //WHEN
        List<Message> messages = messageRepository.findMessages(sender1, "somethingElse");
        //THEN
        assertThat(messages.size()).isEqualTo(0);

        //WHEN
        messages = messageRepository.findMessages("invalid", sender3);
        //THEN
        assertThat(messages.size()).isEqualTo(0);

        //WHEN
        messages = messageRepository.findMessages(sender2, receiver2);
        //THEN
        assertThat(messages.size()).isEqualTo(1);

        //WHEN
        messages = messageRepository.findMessages(receiver2, sender2);
        //THEN
        assertThat(messages.size()).isEqualTo(1);
    }

    private Message given_message(String sender, String receiver) {
        Message message = new Message();
        message.setSenderUserToken(sender);
        message.setReceiverUserToken(receiver);
        message.setCreatedAt(new Date());
        return message;
    }
}