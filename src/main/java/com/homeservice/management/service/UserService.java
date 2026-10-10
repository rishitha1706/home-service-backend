
package com.homeservice.management.service;

import com.homeservice.management.entity.ServiceProvider;
import com.homeservice.management.entity.User;
import com.homeservice.management.repository.BookingRepository;
import com.homeservice.management.repository.ServiceProviderRepository;
import com.homeservice.management.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceProviderRepository providerRepository;
    private final BookingRepository bookingRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ServiceProviderRepository providerRepository,
            BookingRepository bookingRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.providerRepository = providerRepository;
        this.bookingRepository = bookingRepository;
    }

    // CREATE USER / REGISTER
    @Transactional
    public User createUser(
            User user,
            String specialization) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Registration details are required."
            );
        }

        if (user.getName() == null
                || user.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Full name is required."
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        if (user.getRole() == null
                || user.getRole().isBlank()) {
            user.setRole("CUSTOMER");
        }

        String role = user.getRole().trim().toUpperCase();

        if (!role.equals("CUSTOMER")
                && !role.equals("SERVICE_PROVIDER")) {
            throw new IllegalArgumentException(
                    "Only CUSTOMER and SERVICE_PROVIDER registration is allowed."
            );
        }

        user.setRole(role);

        // Validate specialization before saving either record.
        if ("SERVICE_PROVIDER".equals(role)
                && (specialization == null
                || specialization.isBlank())) {

            throw new IllegalArgumentException(
                    "Service type is required for service providers."
            );
        }

        if (userRepository.findByEmail(user.getEmail())
                .isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        // Encode the password before saving.
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepository.save(user);

        // Create provider profile when required.
        if ("SERVICE_PROVIDER".equals(role)) {

            ServiceProvider provider = new ServiceProvider();

            provider.setUser(savedUser);
            provider.setSpecialization(specialization.trim());
            provider.setExperience("New Provider");
            provider.setServiceArea(savedUser.getAddress());
            provider.setAvailable(true);
            provider.setRating(0.0);

            providerRepository.save(provider);
        }

        return savedUser;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET USER BY ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // GET USER BY EMAIL
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // UPDATE USER
    @Transactional
    public User updateUser(Long id, User userDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (userDetails.getName() != null
                && !userDetails.getName().isBlank()) {
            user.setName(userDetails.getName());
        }

        if (userDetails.getEmail() != null
                && !userDetails.getEmail().isBlank()) {
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPhone() != null
                && !userDetails.getPhone().isBlank()) {
            user.setPhone(userDetails.getPhone());
        }

        if (userDetails.getAddress() != null
                && !userDetails.getAddress().isBlank()) {
            user.setAddress(userDetails.getAddress());
        }

        if (userDetails.getRole() != null
                && !userDetails.getRole().isBlank()) {
            user.setRole(userDetails.getRole());
        }

        if (userDetails.getPassword() != null
                && !userDetails.getPassword().isBlank()) {
            user.setPassword(
                    passwordEncoder.encode(
                            userDetails.getPassword()
                    )
            );
        }

        return userRepository.save(user);
    }

    // DELETE USER
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException(
                    "Admin users cannot be deleted."
            );
        }

        Optional<ServiceProvider> provider =
                providerRepository.findByUserId(id);

        if (provider.isPresent()) {

            ServiceProvider serviceProvider = provider.get();

            if (!bookingRepository.findByProviderId(
                    serviceProvider.getId()
            ).isEmpty()) {

                throw new RuntimeException(
                        "Cannot delete this user because the service provider has existing bookings."
                );
            }

            throw new RuntimeException(
                    "Cannot delete this user because they are linked to a service provider profile."
            );
        }

        if (!bookingRepository.findByCustomerId(id).isEmpty()) {
            throw new RuntimeException(
                    "Cannot delete this user because they have existing bookings."
            );
        }

        userRepository.delete(user);
    }
}
