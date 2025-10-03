package com.VitaTrack.Backend.repository;

import com.VitaTrack.Backend.model.PatientAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientAlertRepository extends JpaRepository<PatientAlert, Long> {
    List<PatientAlert> findByDoctorIdOrderByTimestampDesc(Long doctorId);
}
