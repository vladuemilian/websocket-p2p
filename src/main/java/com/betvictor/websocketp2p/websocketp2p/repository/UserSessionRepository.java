package com.betvictor.websocketp2p.websocketp2p.repository;

import com.betvictor.websocketp2p.websocketp2p.domain.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    List<UserSession> findByUserTokenIn(List<String> userTokens);
}
