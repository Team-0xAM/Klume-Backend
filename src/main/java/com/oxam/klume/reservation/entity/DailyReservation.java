package com.oxam.klume.reservation.entity;

import com.oxam.klume.room.entity.DailyAvailableTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Table(name = "daily_reservation")
@NoArgsConstructor
@Entity
public class DailyReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cancelled_at")
    private String cancelledAt;

    @JoinColumn(name = "daily_available_time_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private DailyAvailableTime dailyAvailableTime;

    @JoinColumn(name = "reservation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public DailyReservation(final DailyAvailableTime dailyAvailableTime, final Reservation reservation) {
        this.cancelledAt = null;
        this.dailyAvailableTime = dailyAvailableTime;
        this.reservation = reservation;
    }

    public void updateCancelledAt(final String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void cancel() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(dateTimeFormatter);
        this.cancelledAt = formatted;
    }
}