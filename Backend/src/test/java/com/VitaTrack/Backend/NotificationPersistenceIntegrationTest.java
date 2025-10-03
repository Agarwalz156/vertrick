package com.VitaTrack.Backend;

import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import com.VitaTrack.Backend.repository.NotificationRepository;
import com.VitaTrack.Backend.service.AlertService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {"notification.email.enabled=true"})
@ActiveProfiles("test")
public class NotificationPersistenceIntegrationTest {

    @Autowired
    private AlertService alertService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void alertCreation_persistsNotificationRecord() {
        Doctor d = new Doctor(); d.setEmail("np-doc@example.com"); d.setName("NP Doc"); d.setPassword("pw");
        d = doctorRepository.save(d);
        Patient p = new Patient(); p.setEmail("np-pat@example.com"); p.setName("NP Pat"); p.setPassword("pw"); p.setAssignedDoctorId(d.getId());
        p = patientRepository.save(p);

        alertService.createAndSendAlert(p.getId(), d.getId(), "Persist test");

        var list = notificationRepository.findByDoctorIdOrderByTimestampDesc(d.getId());
        Assertions.assertFalse(list.isEmpty(), "Expected a notification row to be persisted for doctor");
    }
}
