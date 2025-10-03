package com.VitaTrack.Backend.controller;

import com.VitaTrack.Backend.dto.HealthRecordDto;
import com.VitaTrack.Backend.model.HealthRecord;
import com.VitaTrack.Backend.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class HealthRecordController {
    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping("/uploadRecord")
    public ResponseEntity<?> upload(@RequestBody HealthRecordDto dto) {
        HealthRecord r = new HealthRecord();
        r.setPatientId(dto.patientId);
        r.setTimestamp(dto.timestamp);
        r.setHeartRate(dto.heartRate);
        r.setBloodPressure(dto.bloodPressure);
        r.setOxygen(dto.oxygen);
        r.setSugar(dto.sugar);

        HealthRecord saved = healthRecordService.saveRecord(r);
        return ResponseEntity.ok(saved);
    }
}
