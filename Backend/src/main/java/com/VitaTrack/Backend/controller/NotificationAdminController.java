package com.VitaTrack.Backend.controller;

import com.VitaTrack.Backend.model.Notification;
import com.VitaTrack.Backend.repository.NotificationRepository;
import com.VitaTrack.Backend.dto.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class NotificationAdminController {

    @Autowired
    private NotificationRepository notificationRepository;

    // GET /admin/notifications?doctorId=123&page=0&size=20
    @GetMapping("/notifications")
    public Page<NotificationDto> getNotifications(@RequestParam(required = false) Long doctorId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Notification> p;
        if (doctorId != null) {
            p = notificationRepository.findByDoctorIdOrderByTimestampDesc(doctorId, pr);
        } else {
            p = notificationRepository.findAll(pr);
        }
        return p.map(n -> {
            NotificationDto dto = new NotificationDto();
            dto.id = n.getId();
            dto.provider = n.getProvider();
            dto.doctorId = n.getDoctorId();
            dto.patientId = n.getPatientId();
            dto.message = n.getMessage();
            dto.timestamp = n.getTimestamp();
            return dto;
        });
    }
}
