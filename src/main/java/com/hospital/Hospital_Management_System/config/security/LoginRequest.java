package com.hospital.Hospital_Management_System.config.security;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequest {

    private String email;
    private String password;
}
