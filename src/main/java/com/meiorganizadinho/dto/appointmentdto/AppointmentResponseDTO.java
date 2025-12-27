package com.meiorganizadinho.dto.appointmentdto;

import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AppointmentResponseDTO(
        Long id,
        ClientResponseDTO client,
        List<ServiceResponseDTO> services,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        ClientResponseDTO clientDTO = ClientResponseDTO.fromEntity(appointment.getClient());
        List<ServiceResponseDTO> serviceDTOs = appointment.getServices().stream()
                .map(ServiceResponseDTO::fromEntity)
                .toList();

        return new AppointmentResponseDTO(
                appointment.getId(),
                clientDTO,
                serviceDTOs,
                appointment.getDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );
    }
}
