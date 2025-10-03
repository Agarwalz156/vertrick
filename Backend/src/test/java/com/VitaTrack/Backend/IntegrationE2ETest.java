package com.VitaTrack.Backend;

import com.VitaTrack.Backend.dto.HealthRecordDto;
import com.VitaTrack.Backend.dto.LoginRequest;
import com.VitaTrack.Backend.dto.LoginResponse;
import com.VitaTrack.Backend.dto.PatientResponse;
import com.VitaTrack.Backend.model.AlertThreshold;
import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.repository.AlertThresholdRepository;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AlertThresholdRepository thresholdRepository;

    @LocalServerPort
    private int port;

    @Test
    void doctorReceivesAlert_whenPatientUploadsAbnormalRecord() throws Exception {
    String base = restTemplate.getRootUri();

        // register doctor
        Doctor doc = new Doctor();
        doc.setEmail("doc-e2e@example.com");
        doc.setName("Doc E2E");
        doc.setPassword("docpass");
    ResponseEntity<Map<String,Object>> docResp = restTemplate.postForEntity(base + "/doctor/register", doc, (Class<Map<String,Object>>) (Class) Map.class);
        Assertions.assertEquals(HttpStatus.OK, docResp.getStatusCode());
        // find doctor id
        Doctor savedDoc = doctorRepository.findByEmail("doc-e2e@example.com").orElseThrow();

        // register patient assigned to doctor
        Patient p = new Patient();
        p.setEmail("pat-e2e@example.com");
        p.setName("Pat E2E");
        p.setPassword("patpass");
        p.setAssignedDoctorId(savedDoc.getId());
        ResponseEntity<PatientResponse> patResp = restTemplate.postForEntity(base + "/patient/register", p, PatientResponse.class);
        Assertions.assertEquals(HttpStatus.OK, patResp.getStatusCode());
        Long patientId = patResp.getBody().getId();

        // set threshold so that heartRate > 100 triggers
        AlertThreshold t = new AlertThreshold();
        t.setPatientId(patientId);
        t.setMaxHeartRate(100);
        thresholdRepository.save(t);

        // login doctor
        LoginRequest lrDoc = new LoginRequest(); lrDoc.email = "doc-e2e@example.com"; lrDoc.password = "docpass";
        ResponseEntity<LoginResponse> loginDocResp = restTemplate.postForEntity(base + "/auth/login", lrDoc, LoginResponse.class);
        Assertions.assertEquals(HttpStatus.OK, loginDocResp.getStatusCode());
        String token = loginDocResp.getBody().token;

        // prepare STOMP client and subscribe
        BlockingQueue<String> messages = new ArrayBlockingQueue<>(5);

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String wsUrl = "ws://localhost:" + port + "/ws-alerts?token=" + token;
    ListenableFuture<StompSession> fut = stompClient.connect(wsUrl, new StompSessionHandlerAdapter() {});
    StompSession session = fut.get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/alerts/" + savedDoc.getId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) { return Map.class; }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messages.offer(payload.toString());
            }
        });

        // upload abnormal record
        HealthRecordDto hr = new HealthRecordDto();
        hr.patientId = patientId;
        hr.heartRate = 140;
        HttpHeaders hdr = new HttpHeaders(); hdr.setBearerAuth(token); hdr.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HealthRecordDto> req = new HttpEntity<>(hr, hdr);
        ResponseEntity<String> uploadResp = restTemplate.postForEntity(base + "/patient/uploadRecord", req, String.class);
        Assertions.assertEquals(HttpStatus.OK, uploadResp.getStatusCode());

        // wait for message
        String msg = messages.poll(5, TimeUnit.SECONDS);
        Assertions.assertNotNull(msg, "Expected an alert message to be delivered to doctor via STOMP");
        // optional: assert payload contains patientId or message
        Assertions.assertTrue(msg.contains("patientId") || msg.contains("message") || msg.length() > 0);

        session.disconnect();
        stompClient.stop();
    }
}
