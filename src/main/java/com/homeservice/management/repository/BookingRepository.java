
package com.homeservice.management.repository;

import com.homeservice.management.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByProviderId(Long providerId);

    List<Booking> findByStatus(String status);

    List<Booking> findByProviderIdAndBookingDate(
            Long providerId,
            LocalDate bookingDate
    );

    boolean existsByProviderIdAndBookingDateAndBookingTime(
            Long providerId,
            LocalDate bookingDate,
            LocalTime bookingTime
    );
}
