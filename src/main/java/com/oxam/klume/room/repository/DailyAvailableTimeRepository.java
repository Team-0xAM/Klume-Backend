package com.oxam.klume.room.repository;

import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyAvailableTimeRepository extends JpaRepository<DailyAvailableTime, Integer> {
    void deleteAllByAvailableTime(final AvailableTime availableTime);
}
