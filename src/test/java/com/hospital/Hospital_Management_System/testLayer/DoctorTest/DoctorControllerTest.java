package com.hospital.Hospital_Management_System.testLayer.DoctorTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.Hospital_Management_System.controller.DoctorController;
import com.hospital.Hospital_Management_System.dto.DoctorDto;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Specialty;
import com.hospital.Hospital_Management_System.request.DoctorRequest.CreateDoctorRequest;
import com.hospital.Hospital_Management_System.request.DoctorRequest.UpdateDoctorRequest;
import com.hospital.Hospital_Management_System.service.DoctorService.IDoctorService;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DoctorControllerTest {

    private MockMvc mockMvc;
    private IDoctorService doctorService;
    private ObjectMapper objectMapper;

    private CreateDoctorRequest createDoctorRequest;
    private UpdateDoctorRequest updateDoctorRequest;
    private DoctorDto mockDoctor;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setup(){
        doctorService = mock(IDoctorService.class);
        DoctorController doctorController = new DoctorController(doctorService);
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();
        objectMapper = new ObjectMapper();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();

        mockDoctor = DoctorDto.builder()
                .id(1L)
                .name("seif")
                .email("seif@example.com")
                .phone("01020304050")
                .specialty(String.valueOf(Specialty.GENERAL_SURGERY))
                .age(35)
                .yearOfExperience(4)
                .gender(Gender.MALE)
                .address("Cairo")
                .build();

        createDoctorRequest = CreateDoctorRequest.builder()
                .name("seif")
                .email("seif@example.com")
                .phone("01020304050")
                .specialty(Specialty.GENERAL_SURGERY)
                .age(35)
                .yearOfExperience(4)
                .gender(Gender.MALE)
                .address("Cairo")
                .build();

        updateDoctorRequest = UpdateDoctorRequest.builder()
                .name("seif")
                .email("seif@example.com")
                .phone("01020304050")
                .specialty(Specialty.GENERAL_SURGERY)
                .age(35)
                .yearOfExperience(4)
                .gender(Gender.MALE)
                .address("Cairo")
                .build();
    }

    @Test
    void testCreateDoctor_success() throws Exception{
        when(doctorService.createDoctor(any())).thenReturn(mockDoctor);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDoctorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("seif"));

    }

    @Test
    void testUpdateDoctor_Success() throws Exception{
        when(doctorService.updateDoctor(any() , eq(1L))).thenReturn(mockDoctor);

        mockMvc.perform(put("/api/doctors/{id}" , 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDoctorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("seif"));
    }

    @Test
    void testGetDoctorById_Success() throws Exception{
        when(doctorService.getDoctorById(1L)).thenReturn(mockDoctor);

        mockMvc.perform(get("/api/doctors/{id}" , 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("seif"));
    }

    @Test
    void testGatAllDoctors_Success() throws Exception{
        when(doctorService.getAllDoctors()).thenReturn(Collections.singletonList(mockDoctor));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("seif"));
    }

    @Test
    void testGatAllDoctorsByName_Success() throws Exception{
        when(doctorService.getDoctorsByName("seif")).thenReturn(Collections.singletonList(mockDoctor));

        mockMvc.perform(get("/api/doctors")
                        .param("name" , "seif"))
                .andExpect(status().isOk());
    }

    @Test
    void testGatAllDoctorsBySpecialty_Success() throws Exception{
        when(doctorService.getDoctorsBySpecialty(Specialty.GENERAL_SURGERY)).thenReturn(Collections.singletonList(mockDoctor));

        mockMvc.perform(get("/api/doctors")
                        .param("specialty" , String.valueOf(Specialty.GENERAL_SURGERY)))
                .andExpect(status().isOk());
    }

    void testSearchDoctors_Success() throws Exception{
        when(doctorService.searchDoctors(any() , any() , any() , any())).thenReturn(Collections.singletonList(mockDoctor));

        mockMvc.perform(get("/api/doctors/search")
                        .param("name" , "seif")
                        .param("specialty" , String.valueOf(Specialty.GENERAL_SURGERY))
                        .param("gender" , String.valueOf(Gender.MALE))
                        .param("minExperience" , String.valueOf(4)))
                .andExpect(status().isOk());

    }
}
