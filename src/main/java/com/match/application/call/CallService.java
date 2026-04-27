package com.match.application.call;

import com.match.domain.call.CallSession;
import com.match.domain.call.CallType;
import com.match.domain.port.in.CallUseCase;
import com.match.domain.port.out.CallRepositoryPort;
import com.match.domain.port.out.PresencePort;
import com.match.domain.port.out.SignalingPort;
import com.match.domain.shared.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class CallService implements CallUseCase {

    private final CallRepositoryPort calls;
    private final PresencePort presence;
    private final SignalingPort signaling;

    @Value("${app.turn.secret:changeme}")
    private String turnSecret;

    @Value("${app.turn.urls:turn:localhost:3478?transport=udp,turn:localhost:3478?transport=tcp}")
    private String turnUrlsCsv;

    @Value("${app.turn.ttl-seconds:3600}")
    private long turnTtl;

    public CallService(CallRepositoryPort calls, PresencePort presence, SignalingPort signaling) {
        this.calls = calls;
        this.presence = presence;
        this.signaling = signaling;
    }

    @Override
    @Transactional
    public CallSession initiate(UUID callerId, UUID calleeId, CallType type) {
        if (!presence.isOnline(calleeId)) {
            CallSession missed = CallSession.initiate(callerId, calleeId, type).ended("callee_offline");
            return calls.save(missed);
        }
        CallSession session = calls.save(CallSession.initiate(callerId, calleeId, type));
        signaling.sendToUser(calleeId, "{\"type\":\"call-invite\",\"callId\":\"" + session.id()
            + "\",\"from\":\"" + callerId + "\",\"callType\":\"" + type + "\"}");
        return session;
    }

    @Override
    @Transactional
    public CallSession answer(UUID callId, UUID calleeId) {
        CallSession s = calls.findById(callId).orElseThrow(() -> new DomainException("Call not found"));
        if (!s.calleeId().equals(calleeId)) throw new DomainException("Not the callee");
        CallSession answered = calls.save(s.answered());
        signaling.sendToUser(s.callerId(),
            "{\"type\":\"call-accept\",\"callId\":\"" + callId + "\"}");
        return answered;
    }

    @Override
    @Transactional
    public CallSession end(UUID callId, UUID actorId, String reason) {
        CallSession s = calls.findById(callId).orElseThrow(() -> new DomainException("Call not found"));
        CallSession ended = calls.save(s.ended(reason));
        UUID other = actorId.equals(s.callerId()) ? s.calleeId() : s.callerId();
        signaling.sendToUser(other,
            "{\"type\":\"call-end\",\"callId\":\"" + callId + "\",\"reason\":\"" + reason + "\"}");
        return ended;
    }

    /**
     * coturn time-limited credentials:
     *   username = "<expiry-unix>:<userId>"
     *   password = Base64( HMAC-SHA1(turn-secret, username) )
     */
    @Override
    public TurnCredentials issueTurnCredentials(UUID userId) {
        long expiry = Instant.now().getEpochSecond() + turnTtl;
        String username = expiry + ":" + userId;
        String password = hmacSha1Base64(turnSecret, username);
        String[] urls = turnUrlsCsv.split(",");
        return new TurnCredentials(username, password, turnTtl, urls);
    }

    private static String hmacSha1Base64(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute TURN credentials", e);
        }
    }
}

