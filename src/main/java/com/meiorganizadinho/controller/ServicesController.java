package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.servicedto.ServicePostPutRequestDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.service.ServicesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/service")
public class ServicesController {
    private ServicesService servicesService;

    public ServicesController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> post(@Valid @RequestBody ServicePostPutRequestDTO servicePostPutRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicesService.create(servicePostPutRequestDTO));
    }
}
