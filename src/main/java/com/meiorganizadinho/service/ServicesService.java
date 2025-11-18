package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.servicedto.ServicePostPutRequestDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.entity.Services;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.repository.ServiceRepository;
import org.springframework.stereotype.Service;

@Service
public class ServicesService {
    private ServiceRepository serviceRepository;

    public ServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public ServiceResponseDTO create(ServicePostPutRequestDTO servicePostPutRequestDTO) {
        String serviceName = servicePostPutRequestDTO.name();

        boolean isAlreadyServiceExists = serviceRepository.existsByName(serviceName);
        if(isAlreadyServiceExists) {
            throw new BusinessException("Service already exists: " + serviceName);
        }

        Services services = new Services(servicePostPutRequestDTO.name(), servicePostPutRequestDTO.value(), servicePostPutRequestDTO.duration());
        services = serviceRepository.save(services);
        return new ServiceResponseDTO(services.getId(), services.getName(), services.getValue(), services.getDuration());
    }
}
