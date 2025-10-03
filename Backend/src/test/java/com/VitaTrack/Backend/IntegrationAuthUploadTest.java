package com.VitaTrack.Backend;

import com.VitaTrack.Backend.dto.HealthRecordDto;
import com.VitaTrack.Backend.dto.LoginRequest;
import com.VitaTrack.Backend.model.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationAuthUploadTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerLoginUpload() {
        // register
        Patient p = new Patient();
        p.setEmail("testuser@example.com");
        p.setName("Test User");
        p.setPassword("secret123");

        ResponseEntity<Patient> regResp = restTemplate.postForEntity("/patient/register", p, Patient.class);
        Assertions.assertEquals(HttpStatus.OK, regResp.getStatusCode());
        Assertions.assertNotNull(regResp.getBody().getId());

        // login
        LoginRequest lr = new LoginRequest();
        lr.email = "testuser@example.com";
        lr.password = "secret123";
        ResponseEntity<String> loginResp = restTemplate.postForEntity("/auth/login", lr, String.class);
        Assertions.assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        Assertions.assertTrue(loginResp.getBody().contains("token"));

        // extract token
        String token = restTemplate.postForObject("/auth/login", lr, String.class);
        Assertions.assertNotNull(token);

        // upload record with header
        HealthRecordDto hr = new HealthRecordDto();
        hr.patientId = regResp.getBody().getId();
        hr.heartRate = 80;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replaceAll(".*token\\\":\\\"(.*?)\\\".*","$1"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HealthRecordDto> req = new HttpEntity<>(hr, headers);
        ResponseEntity<String> uploadResp = restTemplate.postForEntity("/patient/uploadRecord", req, String.class);
        Assertions.assertEquals(HttpStatus.OK, uploadResp.getStatusCode());
    }
}
