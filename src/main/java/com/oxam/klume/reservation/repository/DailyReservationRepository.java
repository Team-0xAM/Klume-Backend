package com.oxam.klume.reservation.repository;

import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.reservation.entity.Reservation;
import com.oxam.klume.room.entity.AvailableTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DailyReservationRepository extends JpaRepository<DailyReservation, Integer> {
    boolean existsByDailyAvailableTime_AvailableTime(AvailableTime availableTime);

    Optional<DailyReservation> findByDailyAvailableTime_Id(final int dailyAvailableTimeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DailyReservation d WHERE d.dailyAvailableTime.id = :dailyAvailableTimeId")
    Optional<DailyReservation> findByDailyAvailableTime_IdWith(final int dailyAvailableTimeId);

    DailyReservation findByReservation(Reservation reservation);
}
