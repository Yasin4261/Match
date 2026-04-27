package com.match.domain.chat;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void text_factory_creates_sent_text_message() {
        Message m = Message.text(UUID.randomUUID(), UUID.randomUUID(), "hello");
        assertEquals(MessageType.TEXT, m.type());
        assertEquals(MessageStatus.SENT, m.status());
        assertEquals("hello", m.body());
        assertNotNull(m.id());
        assertNotNull(m.createdAt());
    }
}

