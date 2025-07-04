package com.hospital.Hospital_Management_System.notification;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.entity.Patient;
import com.hospital.Hospital_Management_System.repository.PatientRepository;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomReminderScheduler {

    private final PatientRepository patientRepository;
    private final MailService mailService;


    @Scheduled(cron = "0 0 10 * * ?")
    @Transactional
    public void checkRoomBookings() {
        List<Patient> patients = patientRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Patient patient : patients) {
            if (!patient.isAdmitted() || patient.getRoomEndDate() == null) continue;

            LocalDateTime endDate = patient.getRoomEndDate();
            long hoursUntilEnd = java.time.Duration.between(now, endDate).toHours();

            // ✅ تذكير قبل 24 ساعة
            if (hoursUntilEnd > 23 && hoursUntilEnd <= 25) {
                sendReminderEmail(patient);
            }

            // ✅ إنهاء الحجز تلقائيًا إذا انتهى
            if (endDate.isBefore(now)) {
                releaseRoom(patient);
                sendReleaseEmail(patient);
            }
        }
    }

    private void sendReminderEmail(Patient patient) {
        String html = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); max-width: 600px; margin: auto;">
                    <h2 style="color: #f39c12;">⏰ Room Booking Reminder</h2>
                    <p>Dear %s,</p>
                    <p>This is a friendly reminder that your hospital room booking will end tomorrow on <strong>%s</strong>.</p>
                    <p>Please prepare for discharge or request an extension if needed.</p>
                    <p>Thank you for staying with us.</p>
                </div>
            </body>
            </html>
            """.formatted(patient.getName(), patient.getRoomEndDate());

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(patient.getEmail())
                .subject("⏰ Reminder: Room Booking Ends Tomorrow")
                .message(html)
                .contentType("html")
                .build());
    }

    private void sendReleaseEmail(Patient patient) {
        String html = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); max-width: 600px; margin: auto;">
                    <h2 style="color: #27AE60;">✅ Room Booking Ended</h2>
                    <p>Dear %s,</p>
                    <p>Your hospital room booking has ended today.</p>
                    <p>We hope you are feeling better.</p>
                    <p>If you require further care, please contact us.</p>
                </div>
            </body>
            </html>
            """.formatted(patient.getName());

        mailService.sendMail(MailDetailsDto.builder()
                .toMail(patient.getEmail())
                .subject("✅ Room Booking Ended")
                .message(html)
                .contentType("html")
                .build());
    }

    private void releaseRoom(Patient patient) {
        // Free the room
        if (patient.getRoom() != null) {
            patient.getRoom().setAvailableBeds(patient.getRoom().getAvailableBeds() + 1);
        }

        // Clear patient room info
        patient.setAdmitted(false);
        patient.setRoom(null);
        patient.setAdmitDate(null);
        patient.setRoomEndDate(null);

        patientRepository.save(patient);
    }
}
