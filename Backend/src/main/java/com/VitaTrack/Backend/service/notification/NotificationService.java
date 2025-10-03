package com.VitaTrack.Backend.service.notification;

public interface NotificationService {
    void sendNotification(Long doctorId, Long patientId, String message);
}
