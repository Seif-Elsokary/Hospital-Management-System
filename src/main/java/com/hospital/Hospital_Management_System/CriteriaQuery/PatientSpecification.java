package com.hospital.Hospital_Management_System.CriteriaQuery;

import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.enums.Disease;
import com.hospital.Hospital_Management_System.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class PatientSpecification {

    public static Specification<Patient> hasName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Patient> hasDisease(Disease disease) {

        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("disease")), disease);
    }

    public static Specification<Patient> hasGender(Gender gender) {

        return (root, query, cb) ->
                cb.equal(root.get("gender"), gender);
    }

    public static Specification<Patient> hasBloodType(String bloodType) {

        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("bloodType")), bloodType.toLowerCase());
    }

    public static Specification<Patient> hasDateOfRegistration(Date date) {

        return (root, query, cb) ->
                cb.equal(root.get("dateOfRegistration"), date);
    }
}
