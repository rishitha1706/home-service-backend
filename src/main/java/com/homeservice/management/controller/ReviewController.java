package com.homeservice.management.controller;

import com.homeservice.management.entity.Review;
import com.homeservice.management.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestParam Long bookingId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String review) {

        try {

            Review savedReview =
                    reviewService.createReview(
                            bookingId,
                            rating,
                            review
                    );

            return ResponseEntity.ok(savedReview);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getReviewByBookingId(
            @PathVariable Long bookingId) {

        return reviewService
                .getReviewByBookingId(bookingId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Review>>
    getReviewsByProviderId(
            @PathVariable Long providerId) {

        return ResponseEntity.ok(
                reviewService.getReviewsByProviderId(
                        providerId
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Review>>
    getReviewsByCustomerId(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                reviewService.getReviewsByCustomerId(
                        customerId
                )
        );
    }
}