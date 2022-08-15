package com.betvictor.websocketp2p.websocketp2p.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.UUID;

@Document
@Data
@Builder
public class UserSession {
    @Id
    private String id;

    @Indexed
    private String userToken;

    @Indexed
    private String sessionId;

    /**
     * The whole session will be deleted after 10 minutes. This ensures no leftover data will be stored
     * regarding the sessions, which might occur if the client is not gracefully closing the connection.
     * The client is responsible to refresh it's session at a specific interval, bellow 10 minutes.
     */
    @Indexed(name = "expiryIndex", expireAfterSeconds = 600)
    @CreatedDate
    private Date createdAt;
}
