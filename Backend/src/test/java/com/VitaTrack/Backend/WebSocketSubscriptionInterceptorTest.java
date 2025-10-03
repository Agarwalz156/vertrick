package com.VitaTrack.Backend;

import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.security.WebSocketSubscriptionInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketSubscriptionInterceptorTest {
    private DoctorRepository doctorRepository;
    private WebSocketSubscriptionInterceptor interceptor;

    @BeforeEach
    void setup() {
        doctorRepository = Mockito.mock(DoctorRepository.class);
        interceptor = new WebSocketSubscriptionInterceptor(doctorRepository);
    }

    @Test
    void allow_when_doctor_matches() {
        Doctor d = new Doctor(); d.setId(42L); d.setEmail("doc@example.com");
        Mockito.when(doctorRepository.findByEmail("doc@example.com")).thenReturn(Optional.of(d));

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/alerts/42");
        accessor.setUser((Principal) () -> "doc@example.com");
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        MessageChannel channel = Mockito.mock(MessageChannel.class);
        Message<?> out = interceptor.preSend(msg, channel);
        assertNotNull(out, "Subscription should be allowed");
        // ensure no ERROR message sent
        Mockito.verify(channel, Mockito.never()).send(Mockito.any());
    }

    @Test
    void reject_when_doctor_not_found() {
        Mockito.when(doctorRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/alerts/99");
        accessor.setUser((Principal) () -> "unknown@example.com");
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        MessageChannel channel = Mockito.mock(MessageChannel.class);
    @SuppressWarnings({"rawtypes", "unchecked"})
    ArgumentCaptor captor = ArgumentCaptor.forClass(Message.class);
        Message<?> out = interceptor.preSend(msg, channel);
        assertNull(out, "Subscription should be rejected when doctor not found");
    Mockito.verify(channel, Mockito.times(1)).send(Mockito.any());
    @SuppressWarnings({"rawtypes", "unchecked"})
    ArgumentCaptor captor2 = captor;
    Mockito.verify(channel).send((Message<?>) captor2.capture());
    Message<?> sent = (Message<?>) captor2.getValue();
    StompHeaderAccessor ha = MessageHeaderAccessor.getAccessor(sent, StompHeaderAccessor.class);
        assertEquals(StompCommand.ERROR, ha.getCommand());
        assertTrue(ha.getMessage().contains("Unauthorized") || ha.getMessage().length() > 0);
    }

    @Test
    void reject_when_id_mismatch() {
        Doctor d = new Doctor(); d.setId(7L); d.setEmail("doc2@example.com");
        Mockito.when(doctorRepository.findByEmail("doc2@example.com")).thenReturn(Optional.of(d));

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/alerts/8");
        accessor.setUser((Principal) () -> "doc2@example.com");
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        MessageChannel channel = Mockito.mock(MessageChannel.class);
    @SuppressWarnings({"rawtypes", "unchecked"})
    ArgumentCaptor captor = ArgumentCaptor.forClass(Message.class);
        Message<?> out = interceptor.preSend(msg, channel);
        assertNull(out, "Subscription should be rejected when id mismatches");
    Mockito.verify(channel, Mockito.times(1)).send(Mockito.any());
    @SuppressWarnings({"rawtypes", "unchecked"})
    ArgumentCaptor captor2 = captor;
    Mockito.verify(channel).send((Message<?>) captor2.capture());
    Message<?> sent = (Message<?>) captor2.getValue();
    StompHeaderAccessor ha = MessageHeaderAccessor.getAccessor(sent, StompHeaderAccessor.class);
        assertEquals(StompCommand.ERROR, ha.getCommand());
        assertTrue(ha.getMessage().contains("Unauthorized") || ha.getMessage().length() > 0);
    }
}
