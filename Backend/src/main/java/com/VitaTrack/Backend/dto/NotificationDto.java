package com.VitaTrack.Backend.dto;

import java.time.Instant;

public class NotificationDto {
    public Long id;
    public String provider;
    public Long doctorId;
    public Long patientId;
    public String message;
    public Instant timestamp;

    public NotificationDto() {}
}
