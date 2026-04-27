package com.match.adapter.ws.signaling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.match.domain.port.out.PresencePort;
import com.match.domain.port.out.SignalingPort;
import com.match.domain.port.out.TokenPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Raw WebSocket signaling for WebRTC.
 * Handshake URL: /ws/signaling?token=&lt;jwt&gt;
 *
 * Inbound message JSON shape:
 *   { "type": "offer|answer|ice-candidate|call-invite|call-accept|call-reject|call-end",
 *     "to":   "&lt;userId&gt;",
 *     "callId": "...",
 *     "payload": {...}  // SDP / candidate / arbitrary }
 *
 * The server tags each forwarded message with "from" = authenticated userId.
 */
@Component
public class SignalingHandler extends TextWebSocketHandler implements SignalingPort {

    private static final Logger log = LoggerFactory.getLogger(SignalingHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final TokenPort tokens;
    private final PresencePort presence;

    public SignalingHandler(TokenPort tokens, PresencePort presence) {
        this.tokens = tokens;
        this.presence = presence;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = authenticate(session);
        if (userId == null) {
            try { session.close(CloseStatus.POLICY_VIOLATION); } catch (Exception ignored) {}
            return;
        }
        session.getAttributes().put("userId", userId);
        sessions.put(userId, session);
        presence.markOnline(userId, session.getId());
        log.info("Signaling connected user={}", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UUID from = (UUID) session.getAttributes().get("userId");
        if (from == null) return;

        JsonNode root = MAPPER.readTree(message.getPayload());
        String to = root.path("to").asText(null);
        if (to == null) return;
        UUID toId;
        try { toId = UUID.fromString(to); } catch (Exception e) { return; }

        ObjectNode out = (ObjectNode) root;
        out.put("from", from.toString());

        sendToUser(toId, MAPPER.writeValueAsString(out));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID userId = (UUID) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId, session);
            presence.markOffline(userId);
            log.info("Signaling disconnected user={}", userId);
        }
    }

    @Override
    public void sendToUser(UUID userId, String payload) {
        WebSocketSession s = sessions.get(userId);
        if (s != null && s.isOpen()) {
            try { s.sendMessage(new TextMessage(payload)); }
            catch (Exception e) { log.warn("Send failed to {}: {}", userId, e.getMessage()); }
        }
    }

    private UUID authenticate(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String query = uri.getQuery();
        if (query == null) return null;
        for (String part : query.split("&")) {
            int eq = part.indexOf('=');
            if (eq > 0 && "token".equals(part.substring(0, eq))) {
                try { return tokens.validateAccessToken(part.substring(eq + 1)); }
                catch (Exception e) { return null; }
            }
        }
        return null;
    }
}

