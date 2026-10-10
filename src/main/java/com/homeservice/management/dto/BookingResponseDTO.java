
package com.homeservice.management.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingResponseDTO {

    private Long id;

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private Long serviceId;
    private String serviceName;

    private Long providerId;
    private String providerName;
    private String providerPhone;

    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String address;
    private Double totalPrice;
    private String status;

    private String cancellationMessage;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(
            Long id,
            Long customerId,
            String customerName,
            String customerPhone,
            Long serviceId,
            String serviceName,
            Long providerId,
            String providerName,
            String providerPhone,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String address,
            Double totalPrice,
            String status,
            String cancellationMessage) {

        this.id = id;

        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;

        this.serviceId = serviceId;
        this.serviceName = serviceName;

        this.providerId = providerId;
        this.providerName = providerName;
        this.providerPhone = providerPhone;

        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.address = address;
        this.totalPrice = totalPrice;
        this.status = status;
        this.cancellationMessage = cancellationMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationMessage() {
        return cancellationMessage;
    }

    public void setCancellationMessage(String cancellationMessage) {
        this.cancellationMessage = cancellationMessage;
    }
}
