package com.oxam.klume.reservation.entity;

import com.oxam.klume.room.entity.DailyAvailableTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}