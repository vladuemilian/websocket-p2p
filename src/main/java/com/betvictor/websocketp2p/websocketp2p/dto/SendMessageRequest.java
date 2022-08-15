package com.betvictor.websocketp2p.websocketp2p.dto;

import lombok.Data;
import java.util.Date;

@Data
public class SendMessageRequest {
    private String senderUserToken;
    private String receiverUserToken;
    private String body;
    private Date createdAt;
}
