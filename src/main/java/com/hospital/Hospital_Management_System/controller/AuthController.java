package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.config.security.LoginRequest;
import com.hospital.Hospital_Management_System.config.security.jwt.JwtUtils;
import com.hospital.Hospital_Management_System.config.security.userService.CustomerUserDetails;
import com.hospital.Hospital_Management_System.request.DoctorRequest.CreateDoctorRequest;
import com.hospital.Hospital_Management_System.request.PatientRequest.CreatePatientRequest;
import com.hospital.Hospital_Management_System.response.ApiResponse;
import com.hospital.Hospital_Management_System.service.DoctorService.IDoctorService;
import com.hospital.Hospital_Management_System.service.PatientService.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IPatientService patientService;
    private final IDoctorService doctorService;

    public AuthController(AuthenticationManager authenticationManager , JwtUtils jwtUtils ,
                          IPatientService patientService , IDoctorService doctorService){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }


    @PostMapping("/register/patient")
    public ResponseEntity<ApiResponse<String>> registerPatient(@RequestBody CreatePatientRequest patient){
        try {
            patientService.createPatient(patient);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            patient.getEmail(),
                            patient.getPassword()
                    ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateToken(authentication);

            ApiResponse<String> response = new ApiResponse<>(true, "Patient Registered Successfully", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            ApiResponse<String> response = new ApiResponse<>(false, null, "Error during registration: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<ApiResponse<String>> registerDoctor(@RequestBody CreateDoctorRequest doctor){
        try {
            doctorService.createDoctor(doctor);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            doctor.getEmail(),
                            doctor.getPassword()
                    ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateToken(authentication);

            ApiResponse<String> response = new ApiResponse<>(true, "Doctor Registered Successfully", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, null, "Error during registration: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest login){
        try {
            Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(
                           login.getEmail(),
                           login.getPassword()
                   ));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtils.generateToken(authentication);
            CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();

            ApiResponse<String> response = new ApiResponse<>(true, "Login Successfully", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (BadCredentialsException e) {
            ApiResponse<String> response = new ApiResponse<>(false, null, "Invalid credentials: Incorrect email or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, null, "Error during login: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}