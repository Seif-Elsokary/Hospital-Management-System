package com.hospital.Hospital_Management_System.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", unique = true)
    private String roomNumber;

    @Min(value = 1, message = "Total beds must be at least 1")
    @Column(name = "total_beds")
    private int totalBeds;

    @Column(name = "available_beds")
    private int availableBeds;

    // One Room can have many Patients
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Patient> patients;
}
