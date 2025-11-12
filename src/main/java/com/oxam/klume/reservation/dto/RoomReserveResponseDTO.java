package com.oxam.klume.reservation.dto;

import com.oxam.klume.reservation.entity.DailyReservation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class RoomReserveResponseDTO {
    @Schema(description = "회의실 ID")
    private int roomId;

    @Schema(description = "회의실명")
    private String roomName;

    @Schema(description = "예약 취소 일시")
    private String cancelledAt;

    @Schema(description = "예약 ID")
    private int reservationId;

    @Schema(description = "예약일시")
    private String reservedAt;

    @Schema(description = "회의실 이용 사진 url")
    private String reservationImageUrl;

    @Schema(description = "회의실 이용 일시")
    private String availableDate;

    @Schema(description = "회의실 이용 시작 시간")
    private String availableStartTime;

    @Schema(description = "회의실 이용 종료 시간")
    private String availableEndTime;

    @Schema(description = "예약자 조직 멤버 ID")
    private int organizationMemberId;

    @Schema(description = "예약자 조직 멤버 닉네임")
    private String organizationMemberName;

    @Schema(description = "예약자 이메일")
    private String email;

    @Schema(description = "예약자 멤버 ID")
    private int memberId;

    public static RoomReserveResponseDTO of(final DailyReservation dailyReservation) {
        return RoomReserveResponseDTO.builder()
                .roomId(dailyReservation.getReservation().getRoom().getId())
                .roomName(dailyReservation.getReservation().getRoom().getName())
                .cancelledAt(dailyReservation.getCancelledAt())
                .reservationId(dailyReservation.getReservation().getId())
                .reservedAt(dailyReservation.getReservation().getCreatedAt())
                .reservationImageUrl(dailyReservation.getReservation().getImageUrl())
                .availableDate(dailyReservation.getDailyAvailableTime().getDate())
                .availableStartTime(dailyReservation.getDailyAvailableTime().getAvailableStartTime())
                .availableEndTime(dailyReservation.getDailyAvailableTime().getAvailableEndTime())
                .organizationMemberId(dailyReservation.getReservation().getOrganizationMember().getId())
                .organizationMemberName(dailyReservation.getReservation().getOrganizationMember().getNickname())
                .email(dailyReservation.getReservation().getOrganizationMember().getMember().getEmail())
                .memberId(dailyReservation.getReservation().getOrganizationMember().getMember().getId())
                .build();
    }
}