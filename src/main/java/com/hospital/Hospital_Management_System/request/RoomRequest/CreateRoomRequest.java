package com.hospital.Hospital_Management_System.request.RoomRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @Min(value = 1, message = "Total beds must be at least 1")
    private int totalBeds;
}
