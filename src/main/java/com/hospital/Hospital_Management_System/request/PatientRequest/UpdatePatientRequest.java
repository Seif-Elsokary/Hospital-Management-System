package com.hospital.Hospital_Management_System.request.PatientRequest;

import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePatientRequest {

    @NotNull(message = "Id is required")
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

    @NotBlank(message = "Disease is required")
    @Enumerated(EnumType.STRING)
    private Disease disease;

    @NotBlank(message = "Blood type is required")
    private String bloodType;

    @PastOrPresent(message = "Date of registration must be in the past or present")
    private Date dateOfRegistration;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Gender Must Be Selected!")
    private Gender gender;
}
