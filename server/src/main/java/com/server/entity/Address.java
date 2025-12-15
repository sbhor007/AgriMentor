package com.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pinCode;

    @Column(nullable = false)
    private String country;

    private String latitude;
    private String longitude;

    @OneToOne(mappedBy = "address")
    @JsonBackReference
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "farmAddress", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Consultation> consultations;

    @OneToMany(mappedBy = "farmAddress", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<Farmvisit> farmVisits = new ArrayList<>();

}
