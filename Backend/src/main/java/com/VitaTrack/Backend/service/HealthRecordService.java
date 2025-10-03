package com.VitaTrack.Backend.service;

import com.VitaTrack.Backend.model.AlertThreshold;
import com.VitaTrack.Backend.model.HealthRecord;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.repository.AlertThresholdRepository;
import com.VitaTrack.Backend.repository.HealthRecordRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class HealthRecordService {
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    @Autowired
    private AlertThresholdRepository thresholdRepository;
    @Autowired
    private AlertService alertService;
    @Autowired
    private PatientRepository patientRepository;

    public HealthRecord saveRecord(HealthRecord record) {
        if (record.getTimestamp() == null) record.setTimestamp(Instant.now());
        HealthRecord saved = healthRecordRepository.save(record);

        // check thresholds
        Optional<AlertThreshold> tOp = thresholdRepository.findByPatientId(record.getPatientId());
        if (tOp.isPresent()) {
            AlertThreshold t = tOp.get();
            StringBuilder sb = new StringBuilder();
            boolean alert = false;

            if (record.getHeartRate() != null) {
                if (t.getMinHeartRate() != null && record.getHeartRate() < t.getMinHeartRate()) { alert = true; sb.append("Low heart rate. "); }
                if (t.getMaxHeartRate() != null && record.getHeartRate() > t.getMaxHeartRate()) { alert = true; sb.append("High heart rate. "); }
            }
            if (record.getOxygen() != null) {
                if (t.getMinOxygen() != null && record.getOxygen() < t.getMinOxygen()) { alert = true; sb.append("Low oxygen. "); }
            }
            if (record.getSugar() != null) {
                if (t.getMaxSugar() != null && record.getSugar() > t.getMaxSugar()) { alert = true; sb.append("High sugar. "); }
            }

            if (alert) {
                // find patient's assigned doctor
                Optional<Patient> pOp = patientRepository.findById(record.getPatientId());
                Long doctorId = null;
                if (pOp.isPresent()) doctorId = pOp.get().getAssignedDoctorId();

                String message = sb.toString().trim();
                alertService.createAndSendAlert(record.getPatientId(), doctorId == null ? -1L : doctorId, message);
            }
        }

        return saved;
    }
}
