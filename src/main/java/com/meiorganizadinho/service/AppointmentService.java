package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.repository.AppointmentRepository;
import com.meiorganizadinho.repository.ClientRepository;
import com.meiorganizadinho.repository.ServiceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {
    private final ServiceRepository serviceRepository;
    private AppointmentRepository appointmentRepository;
    private ClientRepository clientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ClientRepository clientRepository, ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.serviceRepository = serviceRepository;
    }

    public AppointmentResponseDTO create(AppointmentPostPutRequestDTO appointmentPostPutRequestDTO) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(appointmentPostPutRequestDTO.date(), appointmentPostPutRequestDTO.startTime(), appointmentPostPutRequestDTO.endTime());
        if(!conflicts.isEmpty()) {
            throw new BusinessException("Conflicting appointments in date " + appointmentPostPutRequestDTO.date() + " between " + appointmentPostPutRequestDTO.startTime() + " and " + appointmentPostPutRequestDTO.endTime() + " are found");
        }
        Client client = clientRepository.findById(appointmentPostPutRequestDTO.clientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Appointment appointment = new Appointment();
        appointment.setClient(client);

        for (Long serviceId: appointmentPostPutRequestDTO.servicesId()){
            Services services = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new NotFoundException("Service " + serviceId + " not found"));
            appointment.getServices().add(services);
        }

        if(!appointmentPostPutRequestDTO.startTime().isBefore(appointmentPostPutRequestDTO.endTime())){
            throw new BusinessException("Start time cannot be after end time");
        }

        appointment.setDate(appointmentPostPutRequestDTO.date());
        appointment.setStartTime(appointmentPostPutRequestDTO.startTime());
        appointment.setEndTime(appointmentPostPutRequestDTO.endTime());
        appointment = appointmentRepository.save(appointment);

        return new AppointmentResponseDTO(
                appointment.getId(),
                ClientResponseDTO.fromEntity(appointment.getClient()),
                appointment.getServices().stream()
                        .map(ServiceResponseDTO::fromEntity)
                        .toList(),
                appointment.getDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );
    }

    public List<AppointmentResponseDTO> get(){
        Sort sort = Sort.by(
                Sort.Order.asc("date"),
                Sort.Order.asc("startTime")
        );

        List<Appointment> appointments = appointmentRepository.findAll(sort);
        List<AppointmentResponseDTO> appointmentResponseDTOS = new ArrayList<>();
        for (Appointment appointment: appointments){
            AppointmentResponseDTO appointmentResponseDTO  = new AppointmentResponseDTO(
                    appointment.getId(),
                    ClientResponseDTO.fromEntity(appointment.getClient()),
                    appointment.getServices().stream()
                            .map(ServiceResponseDTO::fromEntity)
                            .toList(),
                    appointment.getDate(),
                    appointment.getStartTime(),
                    appointment.getEndTime()
            );
            appointmentResponseDTOS.add(appointmentResponseDTO);
        }
        return appointmentResponseDTOS;
    }

    public List<AppointmentResponseDTO> getByDate(LocalDate date){
        List<Appointment> appointments = appointmentRepository.findByDateOrderByDateAscStartTimeAsc(date);
        List<AppointmentResponseDTO> appointmentResponseDTOS = new ArrayList<>();
        for (Appointment appointment: appointments){
            AppointmentResponseDTO appointmentResponseDTO  = new AppointmentResponseDTO(
                    appointment.getId(),
                    ClientResponseDTO.fromEntity(appointment.getClient()),
                    appointment.getServices().stream()
                            .map(ServiceResponseDTO::fromEntity)
                            .toList(),
                    appointment.getDate(),
                    appointment.getStartTime(),
                    appointment.getEndTime()
            );
            appointmentResponseDTOS.add(appointmentResponseDTO);
        }
        return appointmentResponseDTOS;
    }

    public List<AppointmentResponseDTO> getByDateAndStartTimeGreaterThan(LocalDate date, LocalTime startTime){
        List<Appointment> appointments = appointmentRepository.findByDateAndStartTimeGreaterThanEqualOrderByDateAscStartTimeAsc(date, startTime);
        List<AppointmentResponseDTO> appointmentResponseDTOS = new ArrayList<>();
        for (Appointment appointment: appointments){
            AppointmentResponseDTO appointmentResponseDTO  = new AppointmentResponseDTO(
                    appointment.getId(),
                    ClientResponseDTO.fromEntity(appointment.getClient()),
                    appointment.getServices().stream()
                            .map(ServiceResponseDTO::fromEntity)
                            .toList(),
                    appointment.getDate(),
                    appointment.getStartTime(),
                    appointment.getEndTime()
            );
            appointmentResponseDTOS.add(appointmentResponseDTO);
        }
        return appointmentResponseDTOS;
    }

    public AppointmentResponseDTO update(Long id, AppointmentPostPutRequestDTO appointmentPostPutRequestDTO){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(appointmentPostPutRequestDTO.date(), appointmentPostPutRequestDTO.startTime(), appointmentPostPutRequestDTO.endTime());
        if(!conflicts.isEmpty()) {
            throw new BusinessException("Conflicting appointments in date " + appointmentPostPutRequestDTO.date() + " between " + appointmentPostPutRequestDTO.startTime() + " and " + appointmentPostPutRequestDTO.endTime() + " are found");
        }
        Client client = clientRepository.findById(appointmentPostPutRequestDTO.clientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if(!appointmentPostPutRequestDTO.startTime().isBefore(appointmentPostPutRequestDTO.endTime())){
            throw new BusinessException("Start time cannot be after end time");
        }

        appointment.setClient(client);
        appointment.getServices().clear();
        for (Long serviceId: appointmentPostPutRequestDTO.servicesId()){
            Services services = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new NotFoundException("Service " + serviceId + " not found"));
            appointment.getServices().add(services);
        }

        appointment.setDate(appointmentPostPutRequestDTO.date());
        appointment.setStartTime(appointmentPostPutRequestDTO.startTime());
        appointment.setEndTime(appointmentPostPutRequestDTO.endTime());
        appointment = appointmentRepository.save(appointment);

        return new AppointmentResponseDTO(
                appointment.getId(),
                ClientResponseDTO.fromEntity(appointment.getClient()),
                appointment.getServices().stream()
                        .map(ServiceResponseDTO::fromEntity)
                        .toList(),
                appointment.getDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );
    }

    public void delete(Long id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        appointmentRepository.delete(appointment);
    }

}
