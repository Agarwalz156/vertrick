package com.VitaTrack.Backend.controller;

import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import com.VitaTrack.Backend.security.JwtUtil;
import com.VitaTrack.Backend.dto.LoginRequest;
import com.VitaTrack.Backend.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        String email = body.email;
        String password = body.password;
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        Object principal = auth.getPrincipal();
        String role = "ROLE_UNKNOWN";
        Long id = null;
        if (principal instanceof UserDetails) {
            UserDetails ud = (UserDetails) principal;
            role = ud.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER");
        }

        Patient p = patientRepository.findByEmail(email).orElse(null);
        Doctor d = doctorRepository.findByEmail(email).orElse(null);
        if (p != null) id = p.getId();
        if (d != null) id = d.getId();

        String token = jwtUtil.generateToken(email, List.of(role), id);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
