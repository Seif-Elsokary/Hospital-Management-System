package com.hospital.Hospital_Management_System.repository;

import com.hospital.Hospital_Management_System.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room , Long> {
}
