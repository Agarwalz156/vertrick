package com.VitaTrack.Backend.dto;

import java.time.Instant;

public class HealthRecordDto {
    public Long patientId;
    public Instant timestamp;
    public Integer heartRate;
    public String bloodPressure;
    public Double oxygen;
    public Double sugar;
}
