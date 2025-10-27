package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.clientdto.ClientPostPutRequestDTO;
import com.meiorganizadinho.dto.clientdto.ClientResponseDTO;
import com.meiorganizadinho.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> post(@Valid @RequestBody ClientPostPutRequestDTO clientPostPutRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(clientPostPutRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> get(@RequestParam(required = false) String name){
        if(name != null) {
            return ResponseEntity.status(HttpStatus.OK).body(clientService.getByName(name));
        }
        return ResponseEntity.status(HttpStatus.OK).body(clientService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> put(@PathVariable int id, @Valid @RequestBody ClientPostPutRequestDTO client) {
        return ResponseEntity.status(HttpStatus.OK).body(clientService.update(id, client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        clientService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
