package com.VitaTrack.Backend.service.notification;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.VitaTrack.Backend.repository.NotificationRepository;
import com.VitaTrack.Backend.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@ConditionalOnProperty(prefix = "notification.fcm", name = "enabled", havingValue = "true", matchIfMissing = false)
public class FcmNotificationService implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendNotification(Long doctorId, Long patientId, String message) {
        Notification n = new Notification();
        n.setProvider("fcm");
        n.setDoctorId(doctorId);
        n.setPatientId(patientId);
        n.setMessage(message);
        n.setTimestamp(java.time.Instant.now());
        notificationRepository.save(n);
    }
}
