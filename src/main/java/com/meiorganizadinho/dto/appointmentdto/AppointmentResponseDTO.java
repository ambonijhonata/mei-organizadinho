package com.meiorganizadinho.dto.appointmentdto;

import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AppointmentResponseDTO(
        ClientResponseDTO client,
        List<ServiceResponseDTO> services,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
