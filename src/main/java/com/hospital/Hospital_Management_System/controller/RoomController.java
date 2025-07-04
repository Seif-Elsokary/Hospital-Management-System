package com.hospital.Hospital_Management_System.controller;

import com.hospital.Hospital_Management_System.dto.RoomDto;
import com.hospital.Hospital_Management_System.request.RoomRequest.CreateRoomRequest;
import com.hospital.Hospital_Management_System.request.RoomRequest.UpdateRoomRequest;
import com.hospital.Hospital_Management_System.service.RoomService.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomDto> createRoom(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id,
                                              @RequestBody UpdateRoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/assign/{patientId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomDto> assignPatientToRoom(@PathVariable Long roomId,
                                                       @PathVariable Long patientId,
                                                       @RequestParam int stayDurationInDays) {
        return ResponseEntity.ok(roomService.assignPatientToRoom(roomId, patientId, stayDurationInDays));
    }

    @PostMapping("/{roomId}/remove/{patientId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> removePatientFromRoom(@PathVariable Long roomId,
                                                      @PathVariable Long patientId) {
        roomService.removePatientFromRoom(roomId, patientId);
        return ResponseEntity.noContent().build();
    }
}
