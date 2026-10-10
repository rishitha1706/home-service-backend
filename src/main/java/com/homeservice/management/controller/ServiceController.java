package com.homeservice.management.controller;

import com.homeservice.management.dto.ServiceResponseDTO;
import com.homeservice.management.entity.Service;
import com.homeservice.management.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(
            @RequestBody Service service) {

        Service savedService = serviceService.createService(service);

        return ResponseEntity.ok(toDTO(savedService));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {

        List<ServiceResponseDTO> services =
                serviceService.getAllServices()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getServiceById(
            @PathVariable Long id) {

        return serviceService.getServiceById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable Long id,
            @RequestBody Service service) {

        try {
            Service updatedService =
                    serviceService.updateService(id, service);

            return ResponseEntity.ok(toDTO(updatedService));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(
            @PathVariable Long id) {

        try {
            serviceService.deleteService(id);

            return ResponseEntity.ok(
                    "Service deleted successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ServiceResponseDTO toDTO(Service service) {

        return new ServiceResponseDTO(
                service.getId(),
                service.getName(),
                service.getCategory(),
                service.getDescription(),
                service.getPrice(),
                service.getAvailable()
        );
    }
}