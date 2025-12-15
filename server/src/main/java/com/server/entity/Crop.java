package com.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.server.enumeration.CropSeason;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Example: "Wheat"

    private String category; // Example: "Cereal", "Vegetable", "Fruit"

    private String type; // Example: "Rabi", "Kharif", "Zaid" (optional)

    private String description; // short description if needed

    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "crop", cascade = CascadeType.ALL)
    @JsonBackReference(value = "consultation-crop")
    private List<Consultation> consultations = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Crop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", consultationsCount=" + (consultations != null ? consultations.size() : 0) +
                '}';
    }

}
