package com.hospital.Hospital_Management_System.testLayer.PatientTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.Hospital_Management_System.controller.PatientController;
import com.hospital.Hospital_Management_System.dto.PatientDto;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.request.PatientRequest.CreatePatientRequest;
import com.hospital.Hospital_Management_System.request.PatientRequest.UpdatePatientRequest;
import com.hospital.Hospital_Management_System.service.PatientService.IPatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PatientControllerTest {

    private MockMvc mockMvc;
    private IPatientService patientService;
    private ObjectMapper objectMapper;

    private CreatePatientRequest createRequest;
    private UpdatePatientRequest updateRequest;
    private PatientDto mockPatient;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setup() {
        patientService = mock(IPatientService.class);
        PatientController patientController = new PatientController(patientService);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();

        mockPatient = PatientDto.builder()
                .name("Ali")
                .email("ali@example.com")
                .phone("01012345678")
                .address("Cairo")
                .age(30)
                .bloodType("A+")
                .disease(Disease.FLU)
                .gender(Gender.MALE)
                .dateOfRegistration(today)
                .build();

        createRequest = CreatePatientRequest.builder()
                .name("Ali")
                .email("ali@example.com")
                .phone("01012345678")
                .address("Cairo")
                .age(30)
                .bloodType("A+")
                .disease(Disease.FLU)
                .gender(Gender.MALE)
                .dateOfRegistration(today)
                .build();

        updateRequest = UpdatePatientRequest.builder()
                .name("Updated Ali")
                .email("ali@example.com")
                .phone("01012345678")
                .address("Giza")
                .age(35)
                .bloodType("A+")
                .disease(Disease.COVID_19)
                .gender(Gender.MALE)
                .dateOfRegistration(today)
                .build();
    }

    @Test
    void testCreatePatient() throws Exception {
        when(patientService.createPatient(any())).thenReturn(mockPatient);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ali"));
    }

    @Test
    void testGetPatientById() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(mockPatient);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ali"));
    }

    @Test
    void testUpdatePatient() throws Exception {
        when(patientService.updatePatient(any(), eq(1L))).thenReturn(mockPatient);

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ali"));
    }

    @Test
    void testDeletePatientById() throws Exception {
        doNothing().when(patientService).deletePatientById(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient deleted successfully"));
    }

    @Test
    void testGetAllPatients() throws Exception {
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ali"));
    }

    @Test
    void testSearchPatients() throws Exception {
        when(patientService.searchPatients(any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/search")
                        .param("name", "Ali")
                        .param("disease", "FLU")
                        .param("gender", "MALE")
                        .param("bloodType", "A+")
                        .param("dateOfRegistration", dateFormat.format(new Date())))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByName() throws Exception {
        when(patientService.getPatientsByName("Ali"))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/by-name")
                        .param("name", "Ali"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByDisease() throws Exception {
        when(patientService.getPatientsByDisease(Disease.FLU))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/by-disease")
                        .param("disease", "FLU"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByNameAndDisease() throws Exception {
        when(patientService.getPatientByNameAndDisease("Ali", Disease.FLU))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/by-name-and-disease")
                        .param("name", "Ali")
                        .param("disease", "FLU"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByDate() throws Exception {
        when(patientService.getPatientByDateOfRegistration(any()))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/by-date")
                        .param("date", dateFormat.format(new Date())))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByDiseaseAndGender() throws Exception {
        when(patientService.getPatientsByDiseaseAndGender(Disease.FLU, Gender.MALE))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/by-disease-and-gender")
                        .param("disease", "FLU")
                        .param("gender", "MALE"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByDiseaseAndGenderAndBloodType() throws Exception {
        when(patientService.getPatientsByDiseaseAndGenderAndBloodType(Disease.FLU, Gender.MALE, "A+"))
                .thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients/by-disease-and-gender-and-bloodType")
                        .param("disease", "FLU")
                        .param("gender", "MALE")
                        .param("bloodType", "A+"))
                .andExpect(status().isOk());
    }
}
