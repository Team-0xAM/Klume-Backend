package com.oxam.klume.reservation.entity;

import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.room.entity.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "reservation")
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String date;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "image_url")
    private String imageUrl;

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @JoinColumn(name = "organization_member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationMember organizationMember;

    public Reservation(final String date, final Room room, final OrganizationMember organizationMember,
                       final String createdAt) {
        this.date = date;
        this.room = room;
        this.organizationMember = organizationMember;
        this.createdAt = createdAt;
    }

    public void updateImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void uploadImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}