package com.server.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.server.enumeration.ConsultationRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String description;
    @Enumerated(EnumType.STRING)
    private ConsultationRequestStatus consultationRequestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    @JsonManagedReference(value = "consultation-farmer")
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id")
    @JsonManagedReference(value = "consultation-consultant")
    private Consultant consultant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    @JsonManagedReference(value = "consultation-crop")
    private Crop crop;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Farmvisit> farmVisits = new ArrayList<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ConsultationReport> consultationReports = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "farm_address_id")
    @JsonManagedReference
    private Address farmAddress;

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "consultation-feedback")
    private Feedback feedback;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", description='" + description + '\'' +
                ", consultationRequestStatus=" + consultationRequestStatus +
                ", farmerId=" + (farmer != null ? farmer.getId() : null) +
                ", consultantId=" + (consultant != null ? consultant.getId() : null) +
                ", cropId=" + (crop != null ? crop.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", closedAt=" + closedAt +
                '}';
    }
}
