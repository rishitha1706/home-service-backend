
package com.homeservice.management.controller;

import com.homeservice.management.dto.BookingResponseDTO;
import com.homeservice.management.dto.ServiceProviderResponseDTO;
import com.homeservice.management.dto.ServiceResponseDTO;
import com.homeservice.management.dto.UserResponseDTO;

import com.homeservice.management.entity.Booking;
import com.homeservice.management.entity.Service;
import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.entity.User;

import com.homeservice.management.service.BookingService;
import com.homeservice.management.service.ServiceProviderService;
import com.homeservice.management.service.ServiceService;
import com.homeservice.management.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final ServiceProviderService providerService;
    private final BookingService bookingService;

    public AdminController(
            UserService userService,
            ServiceService serviceService,
            ServiceProviderService providerService,
            BookingService bookingService) {

        this.userService = userService;
        this.serviceService = serviceService;
        this.providerService = providerService;
        this.bookingService = bookingService;
    }

    // =========================
    // USERS
    // =========================

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {

        List<UserResponseDTO> users =
                userService.getAllUsers()
                        .stream()
                        .map(this::toUserDTO)
                        .toList();

        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        try {
            userService.deleteUser(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "User deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", errorMessage(e))
            );
        }
    }

    // =========================
    // SERVICES
    // =========================

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponseDTO>> getServices() {

        List<ServiceResponseDTO> services =
                serviceService.getAllServices()
                        .stream()
                        .map(this::toServiceDTO)
                        .toList();

        return ResponseEntity.ok(services);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {

        try {
            serviceService.deleteService(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Service deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", errorMessage(e))
            );
        }
    }

    // =========================
    // SERVICE PROVIDERS
    // =========================

    @GetMapping("/providers")
    public ResponseEntity<List<ServiceProviderResponseDTO>>
    getProviders() {

        List<ServiceProviderResponseDTO> providers =
                providerService.getAllProviders()
                        .stream()
                        .map(this::toProviderDTO)
                        .toList();

        return ResponseEntity.ok(providers);
    }

    // =========================
    // CREATE SERVICE PROVIDER
    // =========================

    @PostMapping("/providers")
    public ResponseEntity<?> createProvider(
            @RequestBody Map<String, String> data) {

        try {
            String name = data.get("name");
            String email = data.get("email");
            String phone = data.get("phone");
            String password = data.get("password");
            String address = data.get("address");
            String specialization = data.get("specialization");

            if (isBlank(name)
                    || isBlank(email)
                    || isBlank(phone)
                    || isBlank(password)
                    || isBlank(address)
                    || isBlank(specialization)) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "All service provider fields are required."
                        )
                );
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            user.setAddress(address);
            user.setRole("SERVICE_PROVIDER");

            User savedUser =
                    userService.createUser(user, specialization);

            ServiceProvider provider =
                    providerService.getProviderByUserId(
                            savedUser.getId()
                    );

            if (provider == null) {
                throw new RuntimeException(
                        "Service provider profile was not created."
                );
            }

            return ResponseEntity.ok(toProviderDTO(provider));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", errorMessage(e))
            );
        }
    }

    // =========================
    // DELETE SERVICE PROVIDER
    // =========================

    @DeleteMapping("/providers/{id}")
    public ResponseEntity<?> deleteProvider(
            @PathVariable Long id) {

        try {
            providerService.deleteProvider(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Provider deleted successfully. " +
                            "Upcoming bookings were cancelled."
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", errorMessage(e))
            );
        }
    }

    // =========================
    // BOOKINGS
    // =========================

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getBookings() {

        List<BookingResponseDTO> bookings =
                bookingService.getAllBookings()
                        .stream()
                        .map(this::toBookingDTO)
                        .toList();

        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(
            @PathVariable Long id) {

        try {
            bookingService.deleteBooking(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Booking deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", errorMessage(e))
            );
        }
    }

    // =========================
    // DTO CONVERSIONS
    // =========================

    private UserResponseDTO toUserDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole()
        );
    }

    private ServiceResponseDTO toServiceDTO(Service service) {

        return new ServiceResponseDTO(
                service.getId(),
                service.getName(),
                service.getCategory(),
                service.getDescription(),
                service.getPrice(),
                service.getAvailable()
        );
    }

    private ServiceProviderResponseDTO toProviderDTO(
            ServiceProvider provider) {

        return new ServiceProviderResponseDTO(
                provider.getId(),

                provider.getUser() != null
                        ? provider.getUser().getId()
                        : null,

                provider.getUser() != null
                        ? provider.getUser().getName()
                        : "Unavailable",

                provider.getSpecialization(),
                provider.getExperience(),
                provider.getServiceArea(),
                provider.getAvailable(),
                provider.getRating()
        );
    }

    private BookingResponseDTO toBookingDTO(Booking booking) {

        ServiceProvider provider = booking.getProvider();

        User providerUser = provider != null
                ? provider.getUser()
                : null;

        return new BookingResponseDTO(
                booking.getId(),

                booking.getCustomer() != null
                        ? booking.getCustomer().getId()
                        : null,

                booking.getCustomer() != null
                        ? booking.getCustomer().getName()
                        : "Unknown",

                booking.getCustomer() != null
                        ? booking.getCustomer().getPhone()
                        : null,

                booking.getService() != null
                        ? booking.getService().getId()
                        : null,

                booking.getService() != null
                        ? booking.getService().getName()
                        : "Unavailable",

                provider != null
                        ? provider.getId()
                        : null,

                providerUser != null
                        ? providerUser.getName()
                        : "Provider unavailable",

                providerUser != null
                        ? providerUser.getPhone()
                        : null,

                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getAddress(),
                booking.getTotalPrice(),
                booking.getStatus(),

                // Added 15th constructor argument
                booking.getCancellationMessage()
        );
    }

    // =========================
    // HELPER METHODS
    // =========================

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String errorMessage(RuntimeException e) {

        return e.getMessage() != null
                ? e.getMessage()
                : "An unexpected error occurred.";
    }
}
