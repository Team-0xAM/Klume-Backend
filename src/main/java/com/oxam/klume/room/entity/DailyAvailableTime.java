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

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(name = "open_day")
    private Integer openDay;

    @Column(name = "open_time")
    private String openTime;

    @JoinColumn(name = "available_time_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private AvailableTime availableTime;
}