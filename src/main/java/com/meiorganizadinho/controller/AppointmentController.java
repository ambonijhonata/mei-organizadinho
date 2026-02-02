package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> post(@RequestBody @Valid AppointmentPostPutRequestDTO appointment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(appointment));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> get(@RequestParam(required = false) LocalDate date, @RequestParam(required = false) LocalTime startTime) {
        if (date != null && startTime != null) {
            return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getByDateAndStartTimeGreaterThan(date, startTime));
        } else if (date != null) {
            return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getByDate(date));
        }
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> put(@PathVariable Long id, @Valid @RequestBody AppointmentPostPutRequestDTO appointment) {
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.update(id, appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //
}
