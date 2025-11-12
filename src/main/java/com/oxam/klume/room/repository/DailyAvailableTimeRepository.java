package com.oxam.klume.room.repository;

import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyAvailableTimeRepository extends JpaRepository<DailyAvailableTime, Integer> {
    void deleteAllByAvailableTime(final AvailableTime availableTime);

    @Query("SELECT d FROM DailyAvailableTime d " +
           "JOIN FETCH d.availableTime at " +
           "JOIN FETCH at.room r " +
           "WHERE r.organization.id = :organizationId " +
           "AND d.date >= :today " +
           "ORDER BY d.date ASC, d.availableStartTime ASC")
    List<DailyAvailableTime> findByOrganizationIdAndReservationOpenDay(
            @Param("organizationId") int organizationId,
            @Param("today") String today
    );
}
