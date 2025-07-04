package com.hospital.Hospital_Management_System.dto;

import com.hospital.Hospital_Management_System.enums.Gender;
import lombok.*;

import java.util.List;

@Data
@Builder
public class DoctorDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private int age;
    private int yearOfExperience;
    private String specialty;
    private String address;
    private Gender gender;
    private List<AppointmentDto> appointments;
}
