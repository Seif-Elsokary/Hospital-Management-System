package com.hospital.Hospital_Management_System.entity;

import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Age must be greater than or equal to 0")
    private int age;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(01)[0-9]{9}$", message = "Phone number must be a valid Egyptian number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String password;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Disease is required")
    @Enumerated(EnumType.STRING)
    private Disease disease;

    @NotBlank(message = "Blood type is required")
    private String bloodType;

    @PastOrPresent(message = "Date of registration must be in the past or present")
    private Date dateOfRegistration;

    @NotNull(message = "Gender must be selected")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // ====== Room Booking Info ======
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "is_admitted")
    private boolean isAdmitted = false;

    @Column(name = "admit_date")
    private LocalDateTime admitDate;

    @Column(name = "room_end_date")
    private LocalDateTime roomEndDate;

    // ====== Relationships ======
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<MedicalReport> medicalReports;

    @Enumerated(EnumType.STRING)
    private Role role;
}
