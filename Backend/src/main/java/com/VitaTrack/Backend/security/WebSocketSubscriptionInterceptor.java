package com.VitaTrack.Backend.security;

import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class WebSocketSubscriptionInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebSocketSubscriptionInterceptor.class);

    private final DoctorRepository doctorRepository;

    public WebSocketSubscriptionInterceptor(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            if (StompCommand.SUBSCRIBE.equals(command)) {
                String dest = accessor.getDestination();
                Principal user = accessor.getUser();
                if (dest != null && user != null && dest.startsWith("/topic/alerts/")) {
                    String doctorIdPart = dest.substring(dest.lastIndexOf('/') + 1);
                    log.debug("WS SUBSCRIBE to {} by {}", doctorIdPart, user.getName());

                    // enforce: find doctor by email (principal name) and compare ids
                    Optional<Doctor> dOp = doctorRepository.findByEmail(user.getName());
                    if (dOp.isEmpty()) {
                        log.warn("WS subscribe rejected: no doctor found with email {}", user.getName());
                        sendStompError(channel, accessor, "Unauthorized: no matching doctor account");
                        return null; // reject subscribe
                    }
                    Doctor d = dOp.get();
                    try {
                        Long requestedId = Long.parseLong(doctorIdPart);
                        if (!requestedId.equals(d.getId())) {
                            log.warn("WS subscribe rejected: doctor id mismatch (requested={}, actual={})", requestedId, d.getId());
                            sendStompError(channel, accessor, "Unauthorized: cannot subscribe to another doctor's alerts");
                            return null;
                        }
                        // allowed
                        log.debug("WS subscribe allowed for doctor {}", d.getEmail());
                    } catch (NumberFormatException nfe) {
                        log.warn("WS subscribe rejected: invalid doctor id {}", doctorIdPart);
                        sendStompError(channel, accessor, "Invalid doctor id in destination");
                        return null;
                    }
                }
            }
        }
        return message;
    }

    private void sendStompError(MessageChannel channel, StompHeaderAccessor origAccessor, String message) {
        try {
            StompHeaderAccessor err = StompHeaderAccessor.create(StompCommand.ERROR);
            err.setMessage(message);
            // ensure the client session is targeted
            err.setSessionId(origAccessor.getSessionId());
            err.setLeaveMutable(true);
            Message<byte[]> errMsg = MessageBuilder.createMessage(new byte[0], err.getMessageHeaders());
            channel.send(errMsg);
            log.debug("Sent STOMP ERROR to session {}: {}", origAccessor.getSessionId(), message);
        } catch (Exception e) {
            log.error("Failed to send STOMP error message", e);
        }
    }
}
