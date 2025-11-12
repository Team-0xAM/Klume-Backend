package com.oxam.klume.room.dto;

import com.oxam.klume.room.entity.AvailableTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailableTimeResponseDTO {
    private int id;
    private String name;

    private boolean isMon;
    private boolean isTue;
    private boolean isWed;
    private boolean isThu;
    private boolean isFri;
    private boolean isSat;
    private boolean isSun;

    private String availableStartTime;
    private String availableEndTime;

    private Integer reservationOpenDay;
    private String reservationOpenTime;

    private String repeatStartDay;
    private String repeatEndDay;

    private Integer timeInterval;

    private int roomId;

    public static AvailableTimeResponseDTO of(final AvailableTime availableTime) {
        return AvailableTimeResponseDTO.builder()
                .id(availableTime.getId())
                .name(availableTime.getName())

                .isMon(availableTime.isMon())
                .isTue(availableTime.isTue())
                .isWed(availableTime.isWed())
                .isThu(availableTime.isThu())
                .isFri(availableTime.isFri())
                .isSat(availableTime.isSat())
                .isSun(availableTime.isSun())

                .availableStartTime(availableTime.getAvailableStartTime())
                .availableEndTime(availableTime.getAvailableEndTime())

                .reservationOpenDay(availableTime.getReservationOpenDay())
                .reservationOpenTime(availableTime.getReservationOpenTime())

                .repeatStartDay(availableTime.getRepeatStartDay())
                .repeatEndDay(availableTime.getRepeatEndDay())

                .timeInterval(availableTime.getTimeInterval())

                .roomId(availableTime.getRoom().getId())
                .build();
    }

}
