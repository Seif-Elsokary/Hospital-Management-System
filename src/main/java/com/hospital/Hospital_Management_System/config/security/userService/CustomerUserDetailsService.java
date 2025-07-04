package com.hospital.Hospital_Management_System.config.security.userService;

import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.repository.DoctorRepository;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return patientRepository.findByEmail(email)
                .map(CustomerUserDetails::formPatient)
                .orElseGet(() -> doctorRepository.findByEmail(email)
                        .map(CustomerUserDetails::formDoctor)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User with email: " + email + " not found!")
                        ));
    }
}
