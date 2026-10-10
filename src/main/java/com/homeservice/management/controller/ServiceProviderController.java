
package com.homeservice.management.controller;

import com.homeservice.management.dto.ServiceProviderResponseDTO;
import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.service.ServiceProviderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceProviderController {

    private final ServiceProviderService providerService;

    public ServiceProviderController(
            ServiceProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<?> createProvider(
            @RequestBody ServiceProvider provider) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(providerService.createProvider(provider));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<ServiceProviderResponseDTO>>
    getAllProviders() {

        List<ServiceProviderResponseDTO> result =
                providerService.getAllProviders()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    toDTO(providerService.getProviderById(id))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProviderByUserId(
            @PathVariable Long userId) {
        try {
            return ResponseEntity.ok(
                    toDTO(providerService.getProviderByUserId(userId))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/booking-status")
    public ResponseEntity<?> getBookingStatus(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                Map.of("hasBookings",
                        providerService.hasBookings(id))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(
            @PathVariable Long id,
            @RequestBody ServiceProvider provider) {
        try {
            return ResponseEntity.ok(
                    toDTO(providerService.updateProvider(id, provider))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(
            @PathVariable Long id) {
        try {
            providerService.deleteProvider(id);

            return ResponseEntity.ok(
                    Map.of("message",
                            "Provider deleted and future bookings cancelled")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    private ServiceProviderResponseDTO toDTO(
            ServiceProvider provider) {

        ServiceProviderResponseDTO dto =
                new ServiceProviderResponseDTO();

        dto.setId(provider.getId());

        if (provider.getUser() != null) {
            dto.setUserId(provider.getUser().getId());
            dto.setName(provider.getUser().getName());
        }

        dto.setSpecialization(provider.getSpecialization());
        dto.setExperience(provider.getExperience());
        dto.setServiceArea(provider.getServiceArea());
        dto.setAvailable(provider.getAvailable());
        dto.setRating(provider.getRating());

        return dto;
    }
}
