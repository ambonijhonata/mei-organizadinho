package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.appointmentdto.AppointmentPostPutRequestDTO;
import com.meiorganizadinho.dto.appointmentdto.AppointmentResponseDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.Client;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.exception.ConflictException;
import com.meiorganizadinho.exception.NotFoundException;
import com.meiorganizadinho.messages.AppointmentMessages;
import com.meiorganizadinho.messages.ClientMessages;
import com.meiorganizadinho.messages.ServicesMessages;
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
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ClientRepository clientRepository,
                              ServiceRepository serviceRepository) {

        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.serviceRepository = serviceRepository;
    }

    public AppointmentResponseDTO create(AppointmentPostPutRequestDTO appointmentPostPutRequestDTO) {
        validateAppointmentData(appointmentPostPutRequestDTO);

        validateAppointmentConflict(appointmentPostPutRequestDTO, null);

        Client client = findClientById(appointmentPostPutRequestDTO.clientId());

        Appointment appointment = new Appointment();

        appointmentFromDto(appointment, appointmentPostPutRequestDTO, client);
        appointment = appointmentRepository.save(appointment);

        return AppointmentResponseDTO.fromEntity(appointment);
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
        validateAppointmentData(appointmentPostPutRequestDTO);

        Appointment appointment = findAppointmentById(id);

        validateAppointmentConflict(appointmentPostPutRequestDTO, appointment);

        Client client = findClientById(appointmentPostPutRequestDTO.clientId());

        appointmentFromDto(appointment, appointmentPostPutRequestDTO, client);

        appointment = appointmentRepository.save(appointment);

        return AppointmentResponseDTO.fromEntity((appointment));
    }

    public void delete(Long id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(AppointmentMessages.APPOINTMENT_NOT_FOUND));
        appointmentRepository.delete(appointment);
    }

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(AppointmentMessages.APPOINTMENT_NOT_FOUND));
    }

    private Client findClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException(ClientMessages.CLIENT_NOT_FOUND));
    }

    private void validateAppointmentData(AppointmentPostPutRequestDTO appointmentPostPutRequestDTO) {
        if(!appointmentPostPutRequestDTO.startTime().isBefore(appointmentPostPutRequestDTO.endTime())){
            throw new BusinessException(AppointmentMessages.START_TIME_CANNOT_BE_AFTER_END_TIME);
        }
    }

    private void validateAppointmentConflict(AppointmentPostPutRequestDTO appointmentPostPutRequestDTO, Appointment appointment) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(appointmentPostPutRequestDTO.date(), appointmentPostPutRequestDTO.startTime(), appointmentPostPutRequestDTO.endTime());
        if(!conflicts.isEmpty()) {
            if(appointment != null && !conflicts.getFirst().getId().equals(appointment.getId())){
                throw new ConflictException(AppointmentMessages.getConflictingAppointmentsMessage(appointmentPostPutRequestDTO.date(), appointmentPostPutRequestDTO.startTime(), appointmentPostPutRequestDTO.endTime()));
            } else if(appointment == null){
                throw new ConflictException(AppointmentMessages.getConflictingAppointmentsMessage(appointmentPostPutRequestDTO.date(), appointmentPostPutRequestDTO.startTime(), appointmentPostPutRequestDTO.endTime()));
            }
        }
    }

    private List<Services> validateAndFetchServices(List<Long> servicesId) {
        List<Services> servicesList = new ArrayList<>();
        for (Long serviceId: servicesId){
            Services services = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new NotFoundException(ServicesMessages.getServiceNotFoundMessage(serviceId)));
            servicesList.add(services);
        }
        return servicesList;
    }

    private void appointmentFromDto(Appointment appointment,
                                    AppointmentPostPutRequestDTO appointmentPostPutRequestDTO,
                                    Client client) {
        appointment.setClient(client);
        appointment.getServices().clear();
        appointment.setServices(validateAndFetchServices(appointmentPostPutRequestDTO.servicesId()));
        appointment.setDate(appointmentPostPutRequestDTO.date());
        appointment.setStartTime(appointmentPostPutRequestDTO.startTime());
        appointment.setEndTime(appointmentPostPutRequestDTO.endTime());
    }

}
