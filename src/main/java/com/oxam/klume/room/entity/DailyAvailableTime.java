package com.oxam.klume.room.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@Table(name = "daily_available_time")
@NoArgsConstructor
@Entity
public class DailyAvailableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String date;

    @Column(name = "available_start_time", nullable = false)
    private String availableStartTime;

    @Column(name = "available_end_time", nullable = false)
    private String availableEndTime;

    @Column(name = "reservation_open_day")
    private String reservationOpenDay;

    @Column(name = "reservation_open_time")
    private String reservationOpenTime;

    @JoinColumn(name = "available_time_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private AvailableTime availableTime;

    public void update(
            String date,
            String availableStartTime,
            String availableEndTime,
            String reservationOpenDay,
            String reservationOpenTime,
            AvailableTime availableTime
    ) {
        this.date = date;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationOpenDay = reservationOpenDay;
        this.reservationOpenTime = reservationOpenTime;
        this.availableTime = availableTime;
    }
}