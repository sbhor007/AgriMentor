package com.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.server.enumeration.SoilType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Farmer extends User {

    @Value("50.0")
    private Double farmAreaHectares;
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonBackReference(value = "consultation-farmer")
    private List<Consultation> consultations = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private SoilType soilType;
    @Value("Unknown")
    private String preferredLanguage;
    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Farmer{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", farmAreaHectares=" + farmAreaHectares +
                ", soilType=" + soilType +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
