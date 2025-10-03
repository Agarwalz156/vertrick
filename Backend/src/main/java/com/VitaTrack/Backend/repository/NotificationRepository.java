package com.VitaTrack.Backend.repository;

import com.VitaTrack.Backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDoctorIdOrderByTimestampDesc(Long doctorId);
    Page<Notification> findByDoctorIdOrderByTimestampDesc(Long doctorId, Pageable pageable);
}
