package com.betvictor.websocketp2p.websocketp2p.service;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.domain.User;
import com.betvictor.websocketp2p.websocketp2p.domain.UserSession;
import com.betvictor.websocketp2p.websocketp2p.repository.UserRepository;
import com.betvictor.websocketp2p.websocketp2p.repository.UserSessionRepository;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTests {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserSessionRepository userSessionRepository = Mockito.mock(UserSessionRepository.class);
    private final SocketIOServer socketIOServer = Mockito.mock(SocketIOServer.class);

    private final UserService userService = new UserService(userRepository, userSessionRepository, socketIOServer);

    @Test
    public void should_create_user() {
        //GIVEN
        when(userRepository.save(any(User.class))).thenAnswer((params) -> params.getArgument(0));

        //WHEN
        User user = userService.createUser();

        //THEN
        assertThat(user.getToken()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    public void should_not_authenticate_for_identical_tokens() {
        //WHEN
        boolean authenticated = userService.authenticate("foo", "foo");

        //THEN
        assertThat(authenticated).isFalse();
        verify(userRepository, times(0)).findUserByToken(any(String.class));
    }

    @Test
    public void should_not_authenticate_for_empty_tokens() {
        //WHEN
        boolean authenticated = userService.authenticate("", "foo");

        //THEN
        assertThat(authenticated).isFalse();

        //WHEN
        authenticated = userService.authenticate("foo", "");

        //THEN
        assertThat(authenticated).isFalse();

        verify(userRepository, times(0)).findUserByToken(anyString());
    }

    @Test
    public void should_authenticate_for_valid_tokens() {
        //GIVEN
        when(userRepository.findUserByToken(eq("receiver"))).thenReturn(Optional.of(new User()));
        when(userRepository.findUserByToken("sender")).thenReturn(Optional.of(new User()));

        //WHEN
        boolean authenticated = userService.authenticate("receiver", "sender");

        //THEN
        assertThat(authenticated).isTrue();
    }

    @Test
    public void should_create_user_session() {
        //GIVEN
        UUID sessionId = UUID.randomUUID();
        String userToken = "userToken";

        when(userRepository.findUserByToken(userToken))
                .thenReturn(Optional.of(User.builder()
                        .token(userToken)
                        .build()));

        when(userSessionRepository.save(any(UserSession.class))).thenAnswer((answer) -> answer.getArgument(0));

        //WHEN
        UserSession userSession = userService.createUserSession(sessionId, userToken);

        //THEN
        assertThat(userSession.getSessionId()).isEqualTo(sessionId.toString());
        assertThat(userSession.getUserToken()).isEqualTo(userToken);
    }

    @Test
    public void should_notify() {
        //GIVEN
        String sessionId = UUID.randomUUID().toString();

        String receiverToken = "receiverToken";
        String senderToken = "senderToken";

        Message message = Message.builder()
                .receiverUserToken(receiverToken)
                .senderUserToken(senderToken)
                .body("bodyMessage")
                .build();

        UserSession userSession = UserSession.builder()
                .sessionId(sessionId)
                .userToken(senderToken)
                .build();

        when(userSessionRepository.findByUserTokenIn(List.of(receiverToken, senderToken)))
                .thenReturn(List.of(userSession));

        SocketIOClient validClient = mockClient(sessionId);

        List<SocketIOClient> mockedClients = List.of(validClient, mockClient(UUID.randomUUID().toString()));

        when(socketIOServer.getAllClients()).thenReturn(mockedClients);

        //WHEN
        int count = userService.notifyWithNewMessage(message);

        //THEN
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(validClient).sendEvent(eq("messaging"), stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).contains("bodyMessage");
        assertThat(count).isEqualTo(1);
    }

    private SocketIOClient mockClient(String sessionId) {
        SocketIOClient client = Mockito.mock(SocketIOClient.class);
        when(client.getSessionId()).thenReturn(UUID.fromString(sessionId));

        return client;
    }

}