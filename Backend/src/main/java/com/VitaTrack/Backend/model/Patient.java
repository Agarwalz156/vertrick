package com.VitaTrack.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String password;
    private LocalDate dob;

    // For simplicity store doctor assignment id
    private Long assignedDoctorId;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public Long getAssignedDoctorId() { return assignedDoctorId; }
    public void setAssignedDoctorId(Long assignedDoctorId) { this.assignedDoctorId = assignedDoctorId; }
}
