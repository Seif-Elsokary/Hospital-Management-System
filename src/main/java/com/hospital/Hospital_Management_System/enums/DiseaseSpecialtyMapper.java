package com.hospital.Hospital_Management_System.enums;

import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Specialty;

import java.util.HashMap;
import java.util.Map;

public class DiseaseSpecialtyMapper {

    private static final Map<Disease, Specialty> DISEASE_SPECIALTY_MAP = new HashMap<>();

    static {
        DISEASE_SPECIALTY_MAP.put(Disease.FLU, Specialty.INTERNAL_MEDICINE);
        DISEASE_SPECIALTY_MAP.put(Disease.COVID_19, Specialty.INFECTIOUS_DISEASES);
        DISEASE_SPECIALTY_MAP.put(Disease.DIABETES, Specialty.ENDOCRINOLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.HYPERTENSION, Specialty.CARDIOLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.ASTHMA, Specialty.INTERNAL_MEDICINE);
        DISEASE_SPECIALTY_MAP.put(Disease.CANCER, Specialty.ONCOLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.TUBERCULOSIS, Specialty.INFECTIOUS_DISEASES);
        DISEASE_SPECIALTY_MAP.put(Disease.MALARIA, Specialty.INFECTIOUS_DISEASES);
        DISEASE_SPECIALTY_MAP.put(Disease.MIGRAINE, Specialty.NEUROLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.HEART_DISEASE, Specialty.CARDIOLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.ANXIETY, Specialty.PSYCHIATRY);
        DISEASE_SPECIALTY_MAP.put(Disease.SKIN_RASH, Specialty.DERMATOLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.TONSILLITIS, Specialty.ENT);
        DISEASE_SPECIALTY_MAP.put(Disease.CATARACT, Specialty.OPHTHALMOLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.HEPATITIS, Specialty.GASTROENTEROLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.ULCER, Specialty.GASTROENTEROLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.KIDNEY_STONE, Specialty.UROLOGY);
        DISEASE_SPECIALTY_MAP.put(Disease.TOOTHACHE, Specialty.DENTISTRY);
    }

    public static boolean isMatching(Disease disease, Specialty specialty) {
        return DISEASE_SPECIALTY_MAP.getOrDefault(disease, null) == specialty;
    }

    public static Specialty getSpecialtyByDisease(Disease disease) {
        return DISEASE_SPECIALTY_MAP.getOrDefault(disease, null);
    }
}
