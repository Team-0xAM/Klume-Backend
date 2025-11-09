package com.oxam.klume.room.entity;

import com.oxam.klume.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room",
        uniqueConstraints = @UniqueConstraint(name = "UQ_Room_Name_Per_Org", columnNames = {"organization_id", "name"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int capacity;

    @Setter
    @Column(name = "image_url")
    private String imageUrl;

    @JoinColumn(name = "organization_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    public void updateInfo(String name, String description, int capacity) {
        this.name = name;
        this.description = description;
        this.capacity = capacity;
    }

    public void validateCapacity() {
        if (capacity <= 0) {
            throw new IllegalArgumentException("수용 인원은 1명 이상이어야 합니다.");
        }
    }

    public void assignToOrganization(Organization organization) {
        if (organization == null) throw new IllegalArgumentException("조직은 필수입니다.");
        this.organization = organization;
    }

}