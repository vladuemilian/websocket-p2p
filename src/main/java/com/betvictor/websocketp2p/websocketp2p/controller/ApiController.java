package com.betvictor.websocketp2p.websocketp2p.controller;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import com.betvictor.websocketp2p.websocketp2p.domain.User;
import com.betvictor.websocketp2p.websocketp2p.dto.GetMessagesResponse;
import com.betvictor.websocketp2p.websocketp2p.repository.MessageRepository;
import com.betvictor.websocketp2p.websocketp2p.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppController {

    private final UserService userService;
    private final MessageRepository messageRepository;

    //usually we don't return the User (domain) model directly, but convert it to a UserDto to hide certain internal fields
    @PostMapping("/users")
    public ResponseEntity<User> create() {
        return new ResponseEntity<>(userService.createUser(), HttpStatus.CREATED);
    }

    @GetMapping("/messages/{authenticatedUserToken}/{userToken}")
    public ResponseEntity<GetMessagesResponse> getMessages(@PathVariable("authenticatedUserToken") String authenticatedUserToken, @PathVariable("userToken") String userToken) {
        List<Message> messages = messageRepository.findMessages(authenticatedUserToken, userToken);
        return ResponseEntity.ok(GetMessagesResponse.builder().messages(messages).build());
    }
}
