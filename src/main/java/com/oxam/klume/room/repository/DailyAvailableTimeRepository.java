package com.oxam.klume.room.repository;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DailyAvailableTimeRepository extends JpaRepository<DailyAvailableTime, Integer> {
    void deleteAllByAvailableTime(final AvailableTime availableTime);

    @Query("SELECT r.organization FROM DailyAvailableTime dat JOIN AvailableTime at ON dat.availableTime.id = at.id " +
            "JOIN Room r ON at.room.id = r.id WHERE dat.id=:dailyAvailableTimeId")
    Optional<Organization> findOrganizationByDailyAvailableTimeId(@Param("dailyAvailableTimeId") int dailyAvailableTimeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DailyAvailableTime d WHERE d.id = :dailyAvailableTimeId")
    Optional<DailyAvailableTime> findByIdWithLock(@Param("dailyAvailableTimeId") final int dailyAvailableTimeId);
}