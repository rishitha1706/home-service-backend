
package com.homeservice.management.service;

import com.homeservice.management.entity.Booking;
import com.homeservice.management.entity.Service;
import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.repository.BookingRepository;
import com.homeservice.management.repository.ServiceProviderRepository;
import com.homeservice.management.repository.ServiceRepository;
import com.homeservice.management.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceProviderRepository providerRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            ServiceProviderRepository providerRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.providerRepository = providerRepository;
    }

    public Booking createBooking(Booking booking) {

        if (booking.getCustomer() == null
                || booking.getCustomer().getId() == null) {
            throw new RuntimeException("Customer is required");
        }

        booking.setCustomer(
                userRepository.findById(booking.getCustomer().getId())
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found"))
        );

        if (booking.getService() == null
                || booking.getService().getId() == null) {
            throw new RuntimeException("Service is required");
        }

        booking.setService(
                serviceRepository.findById(booking.getService().getId())
                        .orElseThrow(() ->
                                new RuntimeException("Service not found"))
        );

        if (booking.getBookingDate() == null) {
            throw new RuntimeException("Booking date is required");
        }

        if (booking.getBookingTime() == null) {
            throw new RuntimeException("Booking time is required");
        }

        Service selectedService = booking.getService();
        ServiceProvider selectedProvider = null;

        List<ServiceProvider> providers = providerRepository.findAll();

        for (ServiceProvider provider : providers) {

            if (provider.getAvailable() == null
                    || !provider.getAvailable()) {
                continue;
            }

            if (provider.getUser() == null) {
                continue;
            }

            if (!matchesService(provider, selectedService)) {
                continue;
            }

            boolean alreadyBooked =
                    bookingRepository
                            .existsByProviderIdAndBookingDateAndBookingTime(
                                    provider.getId(),
                                    booking.getBookingDate(),
                                    booking.getBookingTime()
                            );

            if (!alreadyBooked) {
                selectedProvider = provider;
                break;
            }
        }

        if (selectedProvider == null) {
            throw new RuntimeException(
                    "No service provider is available for "
                            + selectedService.getName()
                            + " at this time. Please select another time."
            );
        }

        booking.setProvider(selectedProvider);

        if (booking.getStatus() == null
                || booking.getStatus().isBlank()) {
            booking.setStatus("PENDING");
        }

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> getBookingsByProviderId(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        if (bookingDetails.getStatus() != null
                && !bookingDetails.getStatus().isBlank()) {
            booking.setStatus(bookingDetails.getStatus());
        }

        if (bookingDetails.getAddress() != null) {
            booking.setAddress(bookingDetails.getAddress());
        }

        if (bookingDetails.getTotalPrice() != null) {
            booking.setTotalPrice(bookingDetails.getTotalPrice());
        }

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId, String customerEmail) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        if (booking.getCustomer() == null
                || booking.getCustomer().getEmail() == null
                || customerEmail == null
                || !booking.getCustomer().getEmail()
                        .equalsIgnoreCase(customerEmail)) {
            throw new RuntimeException(
                    "You cannot cancel another customer's booking."
            );
        }

        String status = booking.getStatus();

        if (status == null
                || (!status.equalsIgnoreCase("PENDING")
                && !status.equalsIgnoreCase("CONFIRMED"))) {
            throw new RuntimeException(
                    "Only pending or confirmed bookings can be cancelled."
            );
        }

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        bookingRepository.delete(booking);
    }

    public List<String> getAvailableSlots(LocalDate bookingDate) {

        List<String> availableSlots = new ArrayList<>();

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        List<ServiceProvider> providers = providerRepository.findAll();

        while (!startTime.isAfter(endTime)) {

            boolean slotAvailable = false;

            for (ServiceProvider provider : providers) {

                if (provider.getAvailable() == null
                        || !provider.getAvailable()) {
                    continue;
                }

                if (provider.getUser() == null) {
                    continue;
                }

                boolean alreadyBooked =
                        bookingRepository
                                .existsByProviderIdAndBookingDateAndBookingTime(
                                        provider.getId(),
                                        bookingDate,
                                        startTime
                                );

                if (!alreadyBooked) {
                    slotAvailable = true;
                    break;
                }
            }

            if (slotAvailable) {
                availableSlots.add(startTime.toString());
            }

            startTime = startTime.plusHours(1);
        }

        return availableSlots;
    }

    private boolean matchesService(
            ServiceProvider provider,
            Service service) {

        if (provider.getSpecialization() == null || service == null) {
            return false;
        }

        String specialization =
                provider.getSpecialization().trim().toLowerCase();

        String serviceName =
                service.getName() == null
                        ? ""
                        : service.getName().trim().toLowerCase();

        String category =
                service.getCategory() == null
                        ? ""
                        : service.getCategory().trim().toLowerCase();

        if (specialization.equals(serviceName)) {
            return true;
        }

        if (!category.isEmpty() && specialization.equals(category)) {
            return true;
        }

        return !specialization.isEmpty()
                && serviceName.contains(specialization);
    }
}
