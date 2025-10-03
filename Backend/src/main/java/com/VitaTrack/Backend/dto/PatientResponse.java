package com.VitaTrack.Backend.dto;

import java.time.LocalDate;

public class PatientResponse {
    private Long id;
    private String email;
    private String name;
    private LocalDate dob;

    public PatientResponse() {}

    public PatientResponse(Long id, String email, String name, LocalDate dob) {
        this.id = id; this.email = email; this.name = name; this.dob = dob;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
}
