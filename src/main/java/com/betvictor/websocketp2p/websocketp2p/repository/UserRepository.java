package com.betvictor.websocketp2p.websocketp2p.repository;

import com.betvictor.websocketp2p.websocketp2p.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByToken(String token);
}
