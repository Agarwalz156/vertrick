package com.VitaTrack.Backend.model;

import jakarta.persistence.*;

@Entity
public class AlertThreshold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;

    private Integer minHeartRate;
    private Integer maxHeartRate;
    private Double minOxygen;
    private Double maxSugar;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Integer getMinHeartRate() { return minHeartRate; }
    public void setMinHeartRate(Integer minHeartRate) { this.minHeartRate = minHeartRate; }
    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }
    public Double getMinOxygen() { return minOxygen; }
    public void setMinOxygen(Double minOxygen) { this.minOxygen = minOxygen; }
    public Double getMaxSugar() { return maxSugar; }
    public void setMaxSugar(Double maxSugar) { this.maxSugar = maxSugar; }
}
