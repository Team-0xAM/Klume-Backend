package com.oxam.klume.reservation.service;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.reservation.entity.DailyReservation;

public interface ReservationService {
    DailyReservation reserveRoom(final Member member, final int organizationId, final int roomId,
                                 final int dailyAvailableTimeId);
    DailyReservation cancelReservation(final int reservationId, final int organizationId, final int roomId, final int memberId);
}