package com.oxam.klume.reservation.service;

import com.oxam.klume.common.util.DateUtil;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.exception.OrganizationMemberAccessDeniedException;
import com.oxam.klume.organization.exception.OrganizationMismatchException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.reservation.entity.Reservation;
import com.oxam.klume.reservation.exception.OrganizationMemberBannedException;
import com.oxam.klume.reservation.exception.RoomAlreadyBookedException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.reservation.repository.ReservationRepository;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.exception.DailyAvailableTimeNotFoundException;
import com.oxam.klume.room.exception.RoomNotFoundException;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import com.oxam.klume.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final DailyAvailableTimeRepository dailyAvailableTimeRepository;
    private final DailyReservationRepository dailyReservationRepository;

    @Transactional
    @Override
    public DailyReservation reserveRoom(final Member member, final int organizationId, final int roomId,
                                        final int dailyAvailableTimeId) {
        final Organization organization = findOrganizationById(organizationId);

        final Room room = findRoomById(roomId);

        validateSameOrganization(organization, room.getOrganization());

        final DailyAvailableTime dailyAvailableTime = findDailyReservationById(dailyAvailableTimeId);

        validateSameOrganization(organization, findOrganizationByDailyAvailableTimeId(dailyAvailableTimeId));

        final OrganizationMember organizationMember = findOrganizationByMemberIdAndOrganization(member, organization);

        validateOrganizationMemberNotBanned(organizationMember);

        validateReservationAvailability(dailyAvailableTime);

        final String availableStartDateTime = dailyAvailableTime.getDate() + " "
                + dailyAvailableTime.getAvailableStartTime();

        Reservation reservation = new Reservation(availableStartDateTime, room, organizationMember
                , DateUtil.format(LocalDateTime.now()));

        reservation = reservationRepository.save(reservation);

        return dailyReservationRepository.save(new DailyReservation(dailyAvailableTime, reservation));
    }

    private void validateReservationAvailability(final DailyAvailableTime dailyAvailableTime) {
        final Optional<DailyReservation> dailyReservation = dailyReservationRepository
                .findByDailyAvailableTime_Id(dailyAvailableTime.getId());

        if (dailyReservation.isPresent() && dailyReservation.get().getCancelledAt() == null) {
            throw new RoomAlreadyBookedException();
        }
    }

    public void validateOrganizationMemberNotBanned(final OrganizationMember organizationMember) {
        if (organizationMember.isBanned() &&
                DateUtil.parseToLocalDateTime(organizationMember.getBannedAt()).plusDays(7).isAfter(LocalDateTime.now())) {
            throw new OrganizationMemberBannedException();
        }
    }

    private DailyAvailableTime findDailyReservationById(final int dailyAvailableTimeId) {
        return dailyAvailableTimeRepository.findById(dailyAvailableTimeId)
                .orElseThrow(DailyAvailableTimeNotFoundException::new);
    }

    public void validateSameOrganization(final Organization organization, final Organization targetOrganization) {
        if (organization != targetOrganization) {
            throw new OrganizationMismatchException();
        }
    }

    private OrganizationMember findOrganizationByMemberIdAndOrganization(final Member member, final Organization organization) {
        return organizationMemberRepository.findByMemberIdAndOrganization(member.getId(), organization)
                .orElseThrow(OrganizationMemberAccessDeniedException::new);
    }

    private Organization findOrganizationById(final int organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    private Room findRoomById(final int roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(RoomNotFoundException::new);
    }

    private Organization findOrganizationByDailyAvailableTimeId(final int dailyAvailableTimeId) {
        return dailyAvailableTimeRepository.findOrganizationByDailyAvailableTimeId(dailyAvailableTimeId)
                .orElseThrow(OrganizationNotFoundException::new);
    }
}