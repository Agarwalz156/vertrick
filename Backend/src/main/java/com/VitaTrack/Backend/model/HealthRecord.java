package com.VitaTrack.Backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private Instant timestamp;

    // vitals
    private Integer heartRate;
    private String bloodPressure; // e.g., "120/80"
    private Double oxygen; // SpO2 percentage
    private Double sugar; // mg/dL

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }
    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public Double getOxygen() { return oxygen; }
    public void setOxygen(Double oxygen) { this.oxygen = oxygen; }
    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }
}
