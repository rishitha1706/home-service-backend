
package com.homeservice.management.service;

import com.homeservice.management.entity.Booking;
import com.homeservice.management.entity.Review;
import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.entity.User;

import com.homeservice.management.repository.BookingRepository;
import com.homeservice.management.repository.ReviewRepository;
import com.homeservice.management.repository.ServiceProviderRepository;
import com.homeservice.management.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public ServiceProviderService(
            ServiceProviderRepository providerRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository) {

        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    // CREATE PROVIDER
    public ServiceProvider createProvider(ServiceProvider provider) {

        if (provider.getUser() == null
                || provider.getUser().getId() == null) {

            throw new IllegalArgumentException(
                    "Provider user ID is required"
            );
        }

        User user = userRepository.findById(
                provider.getUser().getId()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        provider.setUser(user);

        if (provider.getAvailable() == null) {
            provider.setAvailable(true);
        }

        if (provider.getRating() == null) {
            provider.setRating(0.0);
        }

        return providerRepository.save(provider);
    }

    // GET ALL PROVIDERS
    public List<ServiceProvider> getAllProviders() {
        return providerRepository.findAll();
    }

    // GET PROVIDER BY ID
    public ServiceProvider getProviderById(Long id) {

        return providerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Provider not found")
                );
    }

    // GET PROVIDER BY USER ID
    public ServiceProvider getProviderByUserId(Long userId) {

        return providerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Provider not found")
                );
    }

    // UPDATE PROVIDER
    @Transactional
    public ServiceProvider updateProvider(
            Long id,
            ServiceProvider updatedProvider) {

        ServiceProvider existing = getProviderById(id);

        existing.setSpecialization(
                updatedProvider.getSpecialization()
        );

        existing.setExperience(
                updatedProvider.getExperience()
        );

        existing.setServiceArea(
                updatedProvider.getServiceArea()
        );

        existing.setAvailable(
                updatedProvider.getAvailable()
        );

        return providerRepository.save(existing);
    }

    // CHECK WHETHER PROVIDER HAS BOOKINGS
    public boolean hasBookings(Long providerId) {

        return !bookingRepository
                .findByProviderId(providerId)
                .isEmpty();
    }

    // PERMANENTLY DELETE PROVIDER WHILE PRESERVING
    // BOOKINGS AND REVIEWS
    @Transactional
    public void deleteProvider(Long id) {

        ServiceProvider provider = providerRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Provider not found")
                );

        LocalDateTime now = LocalDateTime.now();

        String cancellationMessage =
                "Sorry, your booking has been cancelled because " +
                "your service provider is no longer available. " +
                "We apologize for the inconvenience. " +
                "Please book another available provider.";

        // STEP 1: Preserve all bookings.
        // Cancel upcoming bookings that are not completed
        // or already cancelled, then clear provider references.

        List<Booking> bookings =
                bookingRepository.findByProviderId(id);

        for (Booking booking : bookings) {

            String status = booking.getStatus() == null
                    ? "PENDING"
                    : booking.getStatus().trim().toUpperCase();

            boolean finished =
                    "COMPLETED".equals(status)
                    || "CANCELLED".equals(status);

            boolean hasDateAndTime =
                    booking.getBookingDate() != null
                    && booking.getBookingTime() != null;

            boolean upcoming = hasDateAndTime
                    && !LocalDateTime.of(
                            booking.getBookingDate(),
                            booking.getBookingTime()
                    ).isBefore(now);

            if (upcoming && !finished) {
                booking.setStatus("CANCELLED");
                booking.setCancellationMessage(
                        cancellationMessage
                );
            }

            // Keep the booking record but remove its reference
            // to the provider being permanently deleted.
            booking.setProvider(null);
        }

        bookingRepository.saveAllAndFlush(bookings);

        // STEP 2: Preserve all reviews.
        // Remove only the provider relationship, not the reviews.

        List<Review> reviews =
                reviewRepository.findByProviderId(id);

        for (Review review : reviews) {
            review.setProvider(null);
        }

        reviewRepository.saveAllAndFlush(reviews);

        // STEP 3: Permanently delete the provider.
        // Existing bookings and reviews remain in the database.

        providerRepository.delete(provider);
        providerRepository.flush();
    }
}