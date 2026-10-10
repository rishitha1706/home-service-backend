package com.homeservice.management.service;

import com.homeservice.management.entity.Service;
import com.homeservice.management.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public Service updateService(Long id, Service serviceDetails) {

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(serviceDetails.getName());
        service.setCategory(serviceDetails.getCategory());
        service.setDescription(serviceDetails.getDescription());
        service.setPrice(serviceDetails.getPrice());
        service.setAvailable(serviceDetails.getAvailable());

        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        serviceRepository.delete(service);
    }
}