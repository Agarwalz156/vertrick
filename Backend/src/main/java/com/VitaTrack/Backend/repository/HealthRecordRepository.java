package com.VitaTrack.Backend.repository;

import com.VitaTrack.Backend.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByPatientIdOrderByTimestampDesc(Long patientId);
}
