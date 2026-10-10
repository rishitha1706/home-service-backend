package com.homeservice.management.service;

import com.homeservice.management.entity.Booking;
import com.homeservice.management.entity.Review;
import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.repository.BookingRepository;
import com.homeservice.management.repository.ReviewRepository;
import com.homeservice.management.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ServiceProviderRepository providerRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository,
            ServiceProviderRepository providerRepository) {

        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.providerRepository = providerRepository;
    }

    public Review createReview(
            Long bookingId,
            Integer rating,
            String reviewText) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        // Review is allowed only after completion
        if (!"COMPLETED".equalsIgnoreCase(
                booking.getStatus())) {

            throw new RuntimeException(
                    "You can review a service only after it is completed."
            );
        }

        // Prevent duplicate review
        if (reviewRepository.existsByBookingId(bookingId)) {

            throw new RuntimeException(
                    "This booking has already been reviewed."
            );
        }

        // Validate rating
        if (rating == null || rating < 1 || rating > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5."
            );
        }

        if (booking.getProvider() == null) {

            throw new RuntimeException(
                    "No service provider is assigned to this booking."
            );
        }

        Review newReview = new Review();

        newReview.setBooking(booking);
        newReview.setCustomer(booking.getCustomer());
        newReview.setProvider(booking.getProvider());
        newReview.setRating(rating);
        newReview.setReview(reviewText);
        newReview.setCreatedAt(LocalDateTime.now());

        Review savedReview =
                reviewRepository.save(newReview);

        updateProviderRating(
                booking.getProvider().getId()
        );

        return savedReview;
    }

    private void updateProviderRating(Long providerId) {

        ServiceProvider provider =
                providerRepository.findById(providerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service provider not found"
                                ));

        List<Review> reviews =
                reviewRepository.findByProviderId(providerId);

        if (reviews.isEmpty()) {

            provider.setRating(0.0);

        } else {

            double average =
                    reviews.stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);

            double roundedAverage =
                    Math.round(average * 10.0) / 10.0;

            provider.setRating(roundedAverage);
        }

        providerRepository.save(provider);
    }

    public Optional<Review> getReviewByBookingId(
            Long bookingId) {

        return reviewRepository.findByBookingId(
                bookingId
        );
    }

    public List<Review> getReviewsByProviderId(
            Long providerId) {

        return reviewRepository.findByProviderId(
                providerId
        );
    }

    public List<Review> getReviewsByCustomerId(
            Long customerId) {

        return reviewRepository.findByCustomerId(
                customerId
        );
    }
}