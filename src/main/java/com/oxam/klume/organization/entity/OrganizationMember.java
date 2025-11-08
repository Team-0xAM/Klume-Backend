package com.oxam.klume.organization.entity;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "organization_member")
@NoArgsConstructor
@Entity
public class OrganizationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "penalty_count", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private int penaltyCount;

    @Column(name = "is_banned", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isBanned;

    @Column(name = "banned_at")
    private String bannedAt;

    @Column(name = "organization_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationRole role;

    @Column(nullable = false)
    private String nickname;

    @JoinColumn(name = "organization_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "organization_group_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationGroup organizationGroup;
}