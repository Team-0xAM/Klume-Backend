package com.oxam.klume.room.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
    private Integer reservationOpenDay;

    @Column(name = "reservation_open_time")
    private String reservationOpenTime;

    @JoinColumn(name = "available_time_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private AvailableTime availableTime;
}