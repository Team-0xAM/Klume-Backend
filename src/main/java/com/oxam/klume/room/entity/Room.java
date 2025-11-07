package com.oxam.klume.room.entity;

import com.oxam.klume.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "room")
@NoArgsConstructor
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "image_url")
    private String imageUrl;

    @JoinColumn(name = "organization_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_Organization_TO_Room"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;
}