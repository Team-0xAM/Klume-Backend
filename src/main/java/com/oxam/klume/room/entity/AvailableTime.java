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

    @Column(name = "is_mon")
    private boolean isMon;

    @Column(name = "is_tue")
    private boolean isTue;

    @Column(name = "is_wed")
    private boolean isWed;

    @Column(name = "is_thu")
    private boolean isThu;

    @Column(name = "is_fri")
    private boolean isFri;

    @Column(name = "is_sat")
    private boolean isSat;

    @Column(name = "is_sun")
    private boolean isSun;

    @Column(nullable = false)
    private String name;

    @Column(name = "available_start_time", nullable = false)
    private String availableStartTime;

    @Column(name = "available_end_time", nullable = false)
    private String availableEndTime;

    @Column(name = "reservation_open_day")
    private Integer reservationOpenDay;

    @Column(name = "reservation_open_time")
    private String reservationOpenTime;

    @Column(name = "repeat_start_day", nullable = false)
    private String repeatStartDay;

    @Column(name = "repeat_end_day", nullable = false)
    private String repeatEndDay;

    @Column(name = "time_interval")
    private Integer timeInterval;

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    public static AvailableTime create(
            boolean isMon,
            boolean isTue,
            boolean isWed,
            boolean isThu,
            boolean isFri,
            boolean isSat,
            boolean isSun,
            String name,
            String availableStartTime,
            String availableEndTime,
            Integer reservationOpenDay,
            String reservationOpenTime,
            String repeatStartDay,
            String repeatEndDay,
            Integer timeInterval,
            Room room

    ) {
        AvailableTime availableTime = new AvailableTime();
        availableTime.isMon = isMon;
        availableTime.isTue = isTue;
        availableTime.isWed = isWed;
        availableTime.isThu = isThu;
        availableTime.isFri = isFri;
        availableTime.isSat = isSat;
        availableTime.isSun = isSun;
        availableTime.name = name;
        availableTime.availableStartTime = availableStartTime;
        availableTime.availableEndTime = availableEndTime;
        availableTime.reservationOpenDay = reservationOpenDay;
        availableTime.reservationOpenTime = reservationOpenTime;
        availableTime.repeatStartDay = repeatStartDay;
        availableTime.repeatEndDay = repeatEndDay;
        availableTime.timeInterval = timeInterval;
        availableTime.room = room;
        return availableTime;
    }

    public void update(
            boolean isMon,
            boolean isTue,
            boolean isWed,
            boolean isThu,
            boolean isFri,
            boolean isSat,
            boolean isSun,
            String name,
            String availableStartTime,
            String availableEndTime,
            Integer reservationOpenDay,
            String reservationOpenTime,
            String repeatStartDay,
            String repeatEndDay,
            Integer timeInterval
    ) {
        this.isMon = isMon;
        this.isTue = isTue;
        this.isWed = isWed;
        this.isThu = isThu;
        this.isFri = isFri;
        this.isSat = isSat;
        this.isSun = isSun;
        this.name = name;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationOpenDay = reservationOpenDay;
        this.reservationOpenTime = reservationOpenTime;
        this.repeatStartDay = repeatStartDay;
        this.repeatEndDay = repeatEndDay;
        this.timeInterval = timeInterval;
    }

}