package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> post(@RequestBody AppointmentPostPutRequestDTO appointment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(appointment));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> get() {
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.get());
    }
}
