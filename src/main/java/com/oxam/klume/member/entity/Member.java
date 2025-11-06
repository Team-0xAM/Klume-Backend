package com.oxam.klume.member.entity;

import com.oxam.klume.member.entity.enums.Provider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "member")
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_notification_enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isNotificationEnabled;
}