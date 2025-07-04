package com.hospital.Hospital_Management_System.config.security.userService;

import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.entity.Patient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;

    private Collection<GrantedAuthority> authorities;



    public static CustomerUserDetails formPatient(Patient patient){
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(patient.getRole().name());

        return new CustomerUserDetails(
                patient.getId(),
                patient.getEmail(),
                patient.getPassword(),
                List.of(authority)
        );
    }

    public static CustomerUserDetails formDoctor(Doctor doctor){
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(doctor.getRole().name());

        return new CustomerUserDetails(
                doctor.getId(),
                doctor.getEmail(),
                doctor.getPassword(),
                List.of(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
