package com.oxam.klume.room.repository;

import com.oxam.klume.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Optional<Room> findByIdAndOrganizationId(int roomId, int organizationId);
}
