package com.hospital.Hospital_Management_System.CriteriaQuery;

import com.hospital.Hospital_Management_System.entity.Doctor;
import com.hospital.Hospital_Management_System.enums.Gender;
import com.hospital.Hospital_Management_System.enums.Specialty;
import org.springframework.data.jpa.domain.Specification;

public class DoctorSpecification {

    public static Specification<Doctor> hasName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Doctor> hasSpecialty(Specialty specialty) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("specialty")), specialty);
    }

    public static Specification<Doctor> hasGender(Gender gender) {
        return (root, query, cb) ->
                cb.equal(root.get("gender"), gender);
    }

    public static Specification<Doctor> hasMinExperience(Integer experience) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("yearOfExperience"), experience);
    }
}
