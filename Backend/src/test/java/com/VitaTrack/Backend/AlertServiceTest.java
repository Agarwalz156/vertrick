package com.VitaTrack.Backend;

import com.VitaTrack.Backend.model.PatientAlert;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.repository.PatientAlertRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.service.AlertService;
import com.VitaTrack.Backend.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:microhealth;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
public class AlertServiceTest {

    @Autowired
    private AlertService alertService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientAlertRepository patientAlertRepository;

    @MockBean(name = "notificationServiceMock")
    private NotificationService notificationServiceMock;

    @Test
    void createAlert_invokesNotificationServices() {
        Doctor d = new Doctor(); d.setEmail("n-doc@example.com"); d.setName("N Doc"); d.setPassword("pw");
        d = doctorRepository.save(d);
        Patient p = new Patient(); p.setEmail("n-pat@example.com"); p.setName("N Pat"); p.setPassword("pw"); p.setAssignedDoctorId(d.getId());
        p = patientRepository.save(p);

        Long doctorId = d.getId();
        Long patientId = p.getId();

        PatientAlert a = alertService.createAndSendAlert(patientId, doctorId, "Test message for notifications");

        assertNotNull(a.getId());

        ArgumentCaptor<Long> docCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> patCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notificationServiceMock, Mockito.atLeastOnce())
                .sendNotification(docCaptor.capture(), patCaptor.capture(), msgCaptor.capture());

        assertEquals(doctorId, docCaptor.getValue());
        assertEquals(patientId, patCaptor.getValue());
        assertTrue(msgCaptor.getValue().contains("Test message"));
    }
}
