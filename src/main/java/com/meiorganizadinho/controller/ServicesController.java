package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.servicedto.ServicePostPutRequestDTO;
import com.meiorganizadinho.dto.servicedto.ServiceResponseDTO;
import com.meiorganizadinho.service.ServicesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServicesController {
    private ServicesService servicesService;

    public ServicesController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> post(@Valid @RequestBody ServicePostPutRequestDTO servicePostPutRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicesService.create(servicePostPutRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> get(@RequestParam(required = false) String name) {
        if (name != null) {
            return ResponseEntity.status(HttpStatus.OK).body(servicesService.getByName(name));
        }

        return ResponseEntity.status(HttpStatus.OK).body(servicesService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> put(@PathVariable Long id, @Valid @RequestBody ServicePostPutRequestDTO servicePostPutRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(servicesService.update(id, servicePostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> delete(@PathVariable Long id) {
        servicesService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
