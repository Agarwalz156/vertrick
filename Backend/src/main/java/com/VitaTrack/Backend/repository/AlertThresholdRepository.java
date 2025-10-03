package com.VitaTrack.Backend.repository;

import com.VitaTrack.Backend.model.AlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, Long> {
    Optional<AlertThreshold> findByPatientId(Long patientId);
}
