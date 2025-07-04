package com.hospital.Hospital_Management_System.dto;

import lombok.*;

@Data
@Builder
public class RoomDto {
    private Long id;
    private String roomNumber;
    private int totalBeds;
    private int availableBeds;
}
