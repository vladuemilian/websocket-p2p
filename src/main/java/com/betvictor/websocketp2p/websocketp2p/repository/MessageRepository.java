package com.betvictor.websocketp2p.websocketp2p.repository;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    @Query(value = "{$or: [{senderUserToken: ?0, receiverUserToken: ?1}, {senderUserToken: ?1, receiverUserToken: ?0}]}")
    List<Message> findMessages(String senderToken, String receiverToken);
    Optional<Message> findByHash(String hash);
}
