
package com.homeservice.management.controller;

import com.homeservice.management.dto.BookingResponseDTO;
import com.homeservice.management.entity.Booking;
import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.entity.User;
import com.homeservice.management.entity.Service;
import com.homeservice.management.service.BookingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // =========================
    // CREATE BOOKING
    // =========================

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody Booking booking) {

        try {
            Booking savedBooking =
                    bookingService.createBooking(booking);

            return ResponseEntity.ok(toDTO(savedBooking));

        } catch (RuntimeException e) {

            if (e.getMessage() != null
                    && e.getMessage().contains("already booked")) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(e.getMessage());
            }

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // GET ALL BOOKINGS
    // =========================

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {

        List<BookingResponseDTO> bookings =
                bookingService.getAllBookings()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(bookings);
    }

    // =========================
    // GET AVAILABLE SLOTS
    // =========================

    @GetMapping("/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam LocalDate bookingDate) {

        return ResponseEntity.ok(
                bookingService.getAvailableSlots(bookingDate)
        );
    }

    // =========================
    // GET BOOKING BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(
            @PathVariable Long id) {

        return bookingService.getBookingById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // GET BOOKINGS BY CUSTOMER
    // =========================

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponseDTO>>
    getBookingsByCustomerId(
            @PathVariable Long customerId) {

        List<BookingResponseDTO> bookings =
                bookingService.getBookingsByCustomerId(customerId)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(bookings);
    }

    // =========================
    // GET BOOKINGS BY PROVIDER
    // =========================

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<BookingResponseDTO>>
    getBookingsByProviderId(
            @PathVariable Long providerId) {

        List<BookingResponseDTO> bookings =
                bookingService.getBookingsByProviderId(providerId)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(bookings);
    }

    // =========================
    // GET BOOKINGS BY STATUS
    // =========================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponseDTO>>
    getBookingsByStatus(
            @PathVariable String status) {

        List<BookingResponseDTO> bookings =
                bookingService.getBookingsByStatus(status)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(bookings);
    }

    // =========================
    // CANCEL BOOKING
    // =========================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please log in to cancel a booking.");
        }

        try {
            Booking cancelledBooking =
                    bookingService.cancelBooking(
                            id,
                            authentication.getName()
                    );

            return ResponseEntity.ok(toDTO(cancelledBooking));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // UPDATE BOOKING
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking) {

        try {
            Booking updatedBooking =
                    bookingService.updateBooking(id, booking);

            return ResponseEntity.ok(toDTO(updatedBooking));

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    // =========================
    // DELETE BOOKING
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(
            @PathVariable Long id) {

        try {
            bookingService.deleteBooking(id);

            return ResponseEntity.ok(
                    "Booking deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    // =========================
    // CONVERT BOOKING TO DTO
    // =========================

    private BookingResponseDTO toDTO(Booking booking) {

        ServiceProvider provider = booking.getProvider();

        User providerUser = provider != null
                ? provider.getUser()
                : null;

        Service service = booking.getService();

        User customer = booking.getCustomer();

        return new BookingResponseDTO(
                booking.getId(),

                // Customer details
                customer != null
                        ? customer.getId()
                        : null,

                customer != null
                        ? customer.getName()
                        : "Unknown",

                customer != null
                        ? customer.getPhone()
                        : null,

                // Service details
                service != null
                        ? service.getId()
                        : null,

                service != null
                        ? service.getName()
                        : "Unavailable",

                // Provider details
                provider != null
                        ? provider.getId()
                        : null,

                providerUser != null
                        ? providerUser.getName()
                        : "Provider unavailable",

                providerUser != null
                        ? providerUser.getPhone()
                        : null,

                // Booking details
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getAddress(),
                booking.getTotalPrice(),
                booking.getStatus(),

                // Cancellation message for the customer popup
                booking.getCancellationMessage()
        );
    }
}
