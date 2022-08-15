package com.betvictor.websocketp2p.websocketp2p.service;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.domain.User;
import com.betvictor.websocketp2p.websocketp2p.domain.UserSession;
import com.betvictor.websocketp2p.websocketp2p.repository.UserRepository;
import com.betvictor.websocketp2p.websocketp2p.repository.UserSessionRepository;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.diffplug.common.base.Errors;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final SocketIOServer server;

    public User createUser() {
        User user = User.builder()
                .token(UUID.randomUUID().toString())
                .build();

        return userRepository.save(user);
    }

    public boolean authenticate(String receiverUserToken, String senderUserToken) {
        if(StringUtils.isBlank(receiverUserToken) || StringUtils.isBlank(senderUserToken)) {
            return false;
        }

        if(StringUtils.equals(receiverUserToken, senderUserToken)) {
            return false;
        }

        Optional<User> receiverUserOptional = userRepository.findUserByToken(receiverUserToken);
        Optional<User> senderUserOptional = userRepository.findUserByToken(senderUserToken);

        return receiverUserOptional.isPresent() && senderUserOptional.isPresent();
    }

    public UserSession createUserSession(UUID sessionId, String userToken) {
        User user = userRepository.findUserByToken(userToken)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No user was found for token: '%s'", userToken)));

        UserSession userSession = UserSession.builder()
                .userToken(user.getToken())
                .sessionId(sessionId.toString())
                .build();

        return userSessionRepository.save(userSession);
    }

    public int notifyWithNewMessage(Message message) {
        List<String> userSessions = userSessionRepository.findByUserTokenIn(List.of(message.getReceiverUserToken(), message.getSenderUserToken()))
                .stream()
                .map(UserSession::getSessionId).collect(Collectors.toList());

        log.info("Found {} userSessions for tokens: {}, {}", userSessions.size(), message.getReceiverUserToken(), message.getSenderUserToken());

        List<SocketIOClient> clients = server.getAllClients().stream()
                .filter(client -> userSessions.contains(client.getSessionId().toString()))
                .collect(Collectors.toList());

        clients.forEach(client -> {
            try {
                client.sendEvent("messaging", Errors.rethrow().wrap(() -> objectMapper.writeValueAsString(message)).get());
            } catch (Exception e) {
                log.error("Unexpected exception while notifying a client. Reason: {}", e.getMessage(), e);
            }
        });
        return clients.size();
    }
}
