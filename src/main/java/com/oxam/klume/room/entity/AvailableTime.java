package com.oxam.klume.room.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "available_time")
@NoArgsConstructor
@Entity
public class AvailableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_mon", nullable = false)
    private boolean isMon;

    @Column(name = "is_tue", nullable = false)
    private boolean isTue;

    @Column(name = "is_wed", nullable = false)
    private boolean isWed;

    @Column(name = "is_thu", nullable = false)
    private boolean isThu;

    @Column(name = "is_fri", nullable = false)
    private boolean isFri;

    @Column(name = "is_sat", nullable = false)
    private boolean isSat;

    @Column(name = "is_sun", nullable = false)
    private boolean isSun;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(name = "open_day")
    private int openDay;

    @Column(name = "open_time")
    private String openTime;

    @Column(name = "repeat_start_day", nullable = false)
    private String repeatStartDay;

    @Column(name = "repeat_end_day", nullable = false)
    private String repeatEndDay;

    @Column(name = "time_interval", nullable = false)
    private Integer timeInterval;

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
}