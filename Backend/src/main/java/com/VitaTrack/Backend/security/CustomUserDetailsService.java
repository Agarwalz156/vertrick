package com.VitaTrack.Backend.security;

import com.VitaTrack.Backend.model.Doctor;
import com.VitaTrack.Backend.model.Patient;
import com.VitaTrack.Backend.repository.DoctorRepository;
import com.VitaTrack.Backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // try patient
        Patient p = patientRepository.findByEmail(username).orElse(null);
    if (p != null) return new AppUserDetails(p.getEmail(), p.getPassword(), List.of("ROLE_PATIENT"));

        Doctor d = doctorRepository.findByEmail(username).orElse(null);
    if (d != null) return new AppUserDetails(d.getEmail(), d.getPassword(), List.of("ROLE_DOCTOR"));

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
