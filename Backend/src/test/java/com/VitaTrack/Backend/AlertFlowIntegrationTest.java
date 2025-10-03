package com.VitaTrack.Backend;

import com.VitaTrack.Backend.model.AlertThreshold;
import com.VitaTrack.Backend.model.HealthRecord;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.repository.AlertThresholdRepository;
import com.VitaTrack.Backend.repository.PatientAlertRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.service.HealthRecordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class AlertFlowIntegrationTest {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AlertThresholdRepository thresholdRepository;
    @Autowired
    private HealthRecordService healthRecordService;
    @Autowired
    private PatientAlertRepository patientAlertRepository;

    @Test
    void healthRecordTriggersAlert_and_savedInDb() {
        Doctor d = new Doctor(); d.setEmail("i2e-doc@example.com"); d.setName("I2E Doc"); d.setPassword("pw");
        d = doctorRepository.save(d);

        Patient p = new Patient(); p.setEmail("i2e-pat@example.com"); p.setName("I2E Pat"); p.setPassword("pw"); p.setAssignedDoctorId(d.getId());
        p = patientRepository.save(p);

        AlertThreshold t = new AlertThreshold(); t.setPatientId(p.getId()); t.setMaxHeartRate(100);
        thresholdRepository.save(t);

        HealthRecord hr = new HealthRecord(); hr.setPatientId(p.getId()); hr.setHeartRate(150);
        healthRecordService.saveRecord(hr);

        List<?> alerts = patientAlertRepository.findByDoctorIdOrderByTimestampDesc(d.getId());
        Assertions.assertFalse(alerts.isEmpty(), "Alert should be created and saved for doctor");
    }
}
