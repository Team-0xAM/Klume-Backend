package com.oxam.klume.room.repository;

import com.oxam.klume.room.entity.AvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Integer> {
    List<AvailableTime> findAllByRoomId(final int roomId);
}
