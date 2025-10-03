package com.VitaTrack.Backend.service;

import com.VitaTrack.Backend.model.PatientAlert;
import com.VitaTrack.Backend.repository.PatientAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AlertService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PatientAlertRepository alertRepository;

    @Autowired(required = false)
    private java.util.List<com.VitaTrack.Backend.service.notification.NotificationService> notificationServices;

    public PatientAlert createAndSendAlert(Long patientId, Long doctorId, String message) {
        PatientAlert a = new PatientAlert();
        a.setPatientId(patientId);
        a.setDoctorId(doctorId);
        a.setMessage(message);
        a.setTimestamp(Instant.now());
        a.setAcknowledged(false);
        PatientAlert saved = alertRepository.save(a);

        // send to /topic/alerts/{doctorId}
        String destination = "/topic/alerts/" + doctorId;
        messagingTemplate.convertAndSend(destination, saved);

        // send notifications via configured notification services
        if (notificationServices != null) {
            for (com.VitaTrack.Backend.service.notification.NotificationService svc : notificationServices) {
                try {
                    svc.sendNotification(doctorId, patientId, message);
                } catch (Exception e) {
                    // don't fail alert creation if notification fails
                    System.err.println("Notification send failed: " + e.getMessage());
                }
            }
        }

        return saved;
    }
}
