package com.homeservice.management.repository;

import com.homeservice.management.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceProviderRepository
        extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUserId(Long userId);
}