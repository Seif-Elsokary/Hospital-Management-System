package com.hospital.Hospital_Management_System.service.RoomService;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.dto.RoomDto;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.entity.Room;
import com.hospital.Hospital_Management_System.exception.PatientNotFoundException;
import com.hospital.Hospital_Management_System.exception.RoomNotFoundException;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.repository.RoomRepository;
import com.hospital.Hospital_Management_System.request.RoomRequest.CreateRoomRequest;
import com.hospital.Hospital_Management_System.request.RoomRequest.UpdateRoomRequest;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;
    private final PatientRepository patientRepository;
    private final MailService emailService;

    @Override
    public RoomDto createRoom(CreateRoomRequest request) {
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .totalBeds(request.getTotalBeds())
                .availableBeds(request.getTotalBeds())
                .build();

        Room saved = roomRepository.save(room);
        return convertToDto(saved);
    }

    @Override
    public RoomDto updateRoom(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        room.setRoomNumber(request.getRoomNumber());
        room.setTotalBeds(request.getTotalBeds());

        if (room.getAvailableBeds() > request.getTotalBeds()) {
            room.setAvailableBeds(request.getTotalBeds());
        }

        return convertToDto(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        roomRepository.delete(room);
    }

    @Override
    public List<RoomDto> getAllRooms() {
        return convertToDtoList(roomRepository.findAll());
    }

    @Override
    public RoomDto getRoomById(Long id) {
        return convertToDto(roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found")));
    }

    @Override
    public RoomDto assignPatientToRoom(Long roomId, Long patientId, int stayDurationInDays) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (room.getAvailableBeds() <= 0) {
            throw new IllegalStateException("No available beds in this room");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        if (patient.isAdmitted()) {
            throw new IllegalStateException("Patient is already admitted");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(stayDurationInDays);

        patient.setRoom(room);
        patient.setAdmitted(true);
        patient.setAdmitDate(now);
        patient.setRoomEndDate(endDate);
        patientRepository.save(patient);

        room.setAvailableBeds(room.getAvailableBeds() - 1);
        roomRepository.save(room);

        // 📧 Email - Room Booking Confirmation (HTML)
        String html = """
    <html>
    <body style="font-family: Arial, sans-serif;">
        <h2 style="color: #2E86C1;">🏥 Room Booking Confirmation</h2>
        <p>Dear <strong>%s</strong>,</p>
        <p>You have been successfully admitted to <strong>Room %s</strong>.</p>
        <p><strong>Admission Date:</strong> %s</p>
        <p><strong>Expected Discharge Date:</strong> %s</p>
        <p style="color: green;">We wish you a speedy recovery!</p>
    </body>
    </html>
    """.formatted(patient.getName(), room.getRoomNumber(), now, endDate);

        emailService.sendMail(MailDetailsDto.builder()
                .toMail(patient.getEmail())
                .subject("🏥 Room Booking Confirmation")
                .message(html)
                .contentType("html")
                .build());

        return convertToDto(room);
    }

    @Override
    public void removePatientFromRoom(Long roomId, Long patientId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        if (!patient.isAdmitted() || patient.getRoom() == null || !patient.getRoom().getId().equals(roomId)) {
            throw new IllegalStateException("Patient is not admitted in this room");
        }

        patient.setAdmitted(false);
        patient.setRoom(null);
        patient.setAdmitDate(null);
        patient.setRoomEndDate(null);
        patientRepository.save(patient);

        room.setAvailableBeds(room.getAvailableBeds() + 1);
        roomRepository.save(room);

        // 📧 Email - Discharge Notification (HTML)
        String html = """
    <html>
    <body style="font-family: Arial, sans-serif;">
        <h2 style="color: #e74c3c;">Discharge Notification</h2>
        <p>Dear <strong>%s</strong>,</p>
        <p>Your hospital stay in Room <strong>%s</strong> has ended.</p>
        <p>We hope you are feeling better. Wishing you good health!</p>
    </body>
    </html>
    """.formatted(patient.getName(), room.getRoomNumber());

        emailService.sendMail(MailDetailsDto.builder()
                .toMail(patient.getEmail())
                .subject("✅ Room Discharge Notification")
                .message(html)
                .contentType("html")
                .build());
    }


    // ===================== Helper Methods =========================

    private RoomDto convertToDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .totalBeds(room.getTotalBeds())
                .availableBeds(room.getAvailableBeds())
                .build();
    }

    private List<RoomDto> convertToDtoList(List<Room> rooms) {
        return rooms.stream().map(this::convertToDto).toList();
    }
}
