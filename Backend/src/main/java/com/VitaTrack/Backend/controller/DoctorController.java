package com.VitaTrack.Backend.controller;

import com.VitaTrack.Backend.dto.DoctorResponse;
import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Doctor d) {
        if (d.getEmail() == null) return ResponseEntity.badRequest().body("email required");
    if (d.getPassword() != null) d.setPassword(passwordEncoder.encode(d.getPassword()));
    Doctor saved = doctorRepository.save(d);
    DoctorResponse resp = new DoctorResponse(saved.getId(), saved.getEmail(), saved.getName());
    return ResponseEntity.ok(resp);
    }
}
