package com.betvictor.websocketp2p.websocketp2p.dto;

import com.betvictor.websocketp2p.websocketp2p.domain.Message;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GetMessagesResponse {
    List<Message> messages;
}
