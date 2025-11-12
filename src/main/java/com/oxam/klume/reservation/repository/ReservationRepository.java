package com.oxam.klume.reservation.repository;

import com.oxam.klume.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Optional<Reservation> findByIdAndOrganizationMember_Id(int reservationId, int organizationMemberId);
}