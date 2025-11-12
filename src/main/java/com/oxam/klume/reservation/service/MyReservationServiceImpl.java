package com.oxam.klume.reservation.service;

import com.oxam.klume.file.infra.S3Uploader;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.reservation.dao.MyReservationMapper;
import com.oxam.klume.reservation.dto.MyReservationDTO;
import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.reservation.entity.Reservation;
import com.oxam.klume.reservation.exception.ImageUnauthorizedAccessException;
import com.oxam.klume.reservation.exception.ReservationAlreadyStartedException;
import com.oxam.klume.reservation.exception.ReservationImageNotFoundException;
import com.oxam.klume.reservation.exception.ReservationNotFoundException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.reservation.repository.ReservationRepository;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.exception.DailyAvailableTimeNotFoundException;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.oxam.klume.file.FileValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyReservationServiceImpl implements MyReservationService {
    private final MyReservationMapper myReservationMapper;
    private final ReservationRepository reservationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final DailyAvailableTimeRepository dailyAvailableTimeRepository;
    private final DailyReservationRepository dailyReservationRepository;

    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;

    public List<MyReservationDTO> selectMyReservations(final int organizationId, final int memberId) {
        OrganizationMember organizationMember = findOrganizationMemberById(organizationId, memberId);

        return myReservationMapper.selectMyReservations(organizationMember.getId());
    }

    @Transactional
    @Override
    public void cancelReservation(final int reservationId, final int organizationId, final int memberId) {
        OrganizationMember organizationMember = findOrganizationMemberById(organizationId, memberId);
        Reservation reservation = reservationRepository.findByIdAndOrganizationMember_Id(reservationId, organizationMember.getId())
                .orElseThrow(() -> new ReservationNotFoundException("예약이 존재하지 않습니다"));

        DailyReservation dailyReservation = dailyReservationRepository.findByReservation(reservation);
        int dailyAvailableTimeId = dailyReservation.getDailyAvailableTime().getId();
        DailyAvailableTime dailyAvailableTime = dailyAvailableTimeRepository.findById(dailyAvailableTimeId)
                .orElseThrow(() -> new DailyAvailableTimeNotFoundException("해당 예약 시간 정보를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 예약 시작 시간 계산
        String startStr = dailyAvailableTime.getDate() + " " +
                dailyAvailableTime.getAvailableStartTime();
        LocalDateTime reservationStart = LocalDateTime.parse(startStr, formatter);
        
        // 예약 시작 시간이 지났으면 예외 반환
        if (LocalDateTime.now().isAfter(reservationStart)) {
            throw new ReservationAlreadyStartedException();
        }

        // 예약 취소 가능
        dailyReservation.cancel();
        dailyAvailableTime.reopen();

        // 사용 가능 시작 시간 한시간 이내에 취소시 패널티 부여
        LocalDateTime cancelledAt = LocalDateTime.parse(dailyReservation.getCancelledAt(), formatter);
        if (!cancelledAt.isBefore(reservationStart.minusHours(1))) {
            organizationMember.applyPenalty();
        }
    }


    @Transactional
    @Override
    public void enterRoom(final int memberId, final int reservationId, final int organizationId, final MultipartFile file) {
        OrganizationMember organizationMember = findOrganizationMemberById(organizationId, memberId);

        Reservation reservation = findReservationByMemberId(reservationId, organizationMember.getId());

        String s3Url = uploadImage(file);

        reservation.uploadImage(s3Url);
    }

    @Transactional(readOnly = true)
    @Override
    public String getReservationPhoto(int memberId, int reservationId, int organizationId) {
        OrganizationMember organizationMember = findOrganizationMemberById(organizationId, memberId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("예약이 존재하지 않습니다."));

        // 예약한 자거나 조직의 관리자는 인증사진을 볼 수 있다.
        boolean isOwner = reservation.getOrganizationMember().getId() == organizationMember.getId();
        boolean isAdmin = OrganizationRole.ADMIN.equals(organizationMember.getRole());

        if (!isOwner && !isAdmin) {
            throw new ImageUnauthorizedAccessException();
        }

        String imageUrl = reservation.getImageUrl();
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ReservationImageNotFoundException();
        }

        return imageUrl;
    }


    private String uploadImage(final MultipartFile file) {
        if (file != null) {
            fileValidator.validateImage(file);

            return s3Uploader.upload("reservation/", file);
        }
        return null;
    }

    private OrganizationMember findOrganizationMemberById(final int organizationId, final int memberId) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new OrganizationNotFoundException("사용자가 가입하지 않은 조직입니다."));
    }

    private Reservation findReservationByMemberId(final int reservationId, final int organizationMemberId) {
        return reservationRepository.findByIdAndOrganizationMember_Id(reservationId, organizationMemberId)
                .orElseThrow(() -> new ReservationNotFoundException("해당 사용자의 예약이 없습니다."));
    }
}
