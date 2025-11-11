package com.oxam.klume.reservation.repository;

import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.room.entity.AvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyReservationRepository extends JpaRepository<DailyReservation, Integer> {
    boolean existsByDailyAvailableTime_AvailableTime(AvailableTime availableTime);

    Optional<DailyReservation> findByDailyAvailableTime_Id(int dailyAvailableTimeId);
}
