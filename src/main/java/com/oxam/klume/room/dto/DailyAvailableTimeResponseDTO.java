package com.oxam.klume.room.dto;

import com.oxam.klume.room.entity.DailyAvailableTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyAvailableTimeResponseDTO {
    private int id;
    private int available_time_id;
    private String date;
    private String available_start_time;
    private String available_end_time;
    private String reservation_open_day;
    private String reservation_open_time;
    private String room_name;

    public static DailyAvailableTimeResponseDTO of(final DailyAvailableTime dailyAvailableTime) {
        return DailyAvailableTimeResponseDTO.builder()
                .id(dailyAvailableTime.getId())
                .date(dailyAvailableTime.getDate())
                .available_time_id(dailyAvailableTime.getAvailableTime().getId())
                .available_start_time(dailyAvailableTime.getAvailableStartTime())
                .available_end_time(dailyAvailableTime.getAvailableEndTime())
                .reservation_open_day(dailyAvailableTime.getReservationOpenDay())
                .reservation_open_time(dailyAvailableTime.getReservationOpenTime())
                .room_name(dailyAvailableTime.getAvailableTime().getRoom().getName())
                .build();
    }
}
