package com.match.adapter.ws.chat;

import com.match.domain.chat.Message;
import com.match.domain.port.in.ChatUseCase;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

/**
 * STOMP chat. Client subscribes to /topic/conv.{conversationId}
 * and sends to /app/chat.send.{conversationId}
 */
@Controller
public class ChatStompController {

    private final ChatUseCase chat;
    private final SimpMessagingTemplate broker;

    public ChatStompController(ChatUseCase chat, SimpMessagingTemplate broker) {
        this.chat = chat;
        this.broker = broker;
    }

    public record InboundMessage(String body) {}

    @MessageMapping("/chat.send.{conversationId}")
    public void send(@DestinationVariable UUID conversationId,
                     @Payload InboundMessage in,
                     Principal principal) {
        UUID sender = UUID.fromString(principal.getName());
        Message saved = chat.send(new ChatUseCase.SendMessageCommand(sender, conversationId, in.body()));
        broker.convertAndSend("/topic/conv." + conversationId, saved);
    }
}

