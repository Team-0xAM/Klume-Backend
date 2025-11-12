package com.oxam.klume.room.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    public void reopen() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.reservationOpenDay = LocalDate.now().format(dateFormatter);
        this.reservationOpenTime = LocalTime.now().format(timeFormatter);
    }

    public LocalDateTime getStartDateTime() {
        LocalDate date = LocalDate.parse(this.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime time = LocalTime.parse(this.availableStartTime, DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(date, time);
    }

}