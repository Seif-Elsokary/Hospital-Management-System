package com.hospital.Hospital_Management_System.service.RoomService;

import com.hospital.Hospital_Management_System.dto.RoomDto;
import com.hospital.Hospital_Management_System.request.RoomRequest.CreateRoomRequest;
import com.hospital.Hospital_Management_System.request.RoomRequest.UpdateRoomRequest;

import java.util.List;

public interface IRoomService {

    RoomDto createRoom(CreateRoomRequest request);

    RoomDto updateRoom(Long id, UpdateRoomRequest request);

    void deleteRoom(Long id);

    List<RoomDto> getAllRooms();

    RoomDto getRoomById(Long id);

    RoomDto assignPatientToRoom(Long roomId, Long patientId, int stayDurationInDays);

    void removePatientFromRoom(Long roomId, Long patientId);
}
