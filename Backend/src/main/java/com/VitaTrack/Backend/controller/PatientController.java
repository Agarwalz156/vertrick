package com.VitaTrack.Backend.controller;

import com.VitaTrack.Backend.dto.PatientResponse;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Patient p) {
        if (p.getEmail() == null) return ResponseEntity.badRequest().body("email required");
    // hash password
    if (p.getPassword() != null) p.setPassword(passwordEncoder.encode(p.getPassword()));
    Patient saved = patientRepository.save(p);
    PatientResponse resp = new PatientResponse(saved.getId(), saved.getEmail(), saved.getName(), saved.getDob());
    return ResponseEntity.ok(resp);
    }
}
