package com.hospital.Hospital_Management_System.dto;

import com.hospital.Hospital_Management_System.entity.Room;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class PatientDto {

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

    @Column(name = "is_admitted")
    private boolean isAdmitted = false;

    @Column(name = "admit_date")
    private LocalDateTime admitDate;

    @Column(name = "room_end_date")
    private LocalDateTime roomEndDate;

    private List<AppointmentDto> appointments;

}
