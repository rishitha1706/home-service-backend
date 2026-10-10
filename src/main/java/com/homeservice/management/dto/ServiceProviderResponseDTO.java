package com.homeservice.management.dto;

public class ServiceProviderResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String specialization;
    private String experience;
    private String serviceArea;
    private Boolean available;
    private Double rating;

    public ServiceProviderResponseDTO() {
    }

    public ServiceProviderResponseDTO(
            Long id,
            Long userId,
            String name,
            String specialization,
            String experience,
            String serviceArea,
            Boolean available,
            Double rating) {

        this.id = id;
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.serviceArea = serviceArea;
        this.available = available;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}