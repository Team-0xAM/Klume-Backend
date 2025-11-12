package com.oxam.klume.organization.entity;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Builder
    public OrganizationMember(final OrganizationRole role, final String nickname, final Organization organization,
                              final Member member, final OrganizationGroup organizationGroup) {
        this.penaltyCount = 0;
        this.isBanned = false;
        this.bannedAt = null;
        this.role = role;
        this.nickname = nickname;
        this.organization = organization;
        this.member = member;
        this.organizationGroup = organizationGroup;
    }

    public void updateRole(final OrganizationRole role) {
        this.role = role;
    }

    public void updateOrganizationGroup(final OrganizationGroup organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public void updatePenaltyStatus(final int penaltyCount, final boolean isBanned, final String bannedAt) {
        this.penaltyCount = penaltyCount;
        this.isBanned = isBanned;
        this.bannedAt = bannedAt;
    }

    public void applyPenalty() {
        this.penaltyCount++;
        checkBanStatus();
    }

    private void checkBanStatus() {
        if (this.penaltyCount >= 3 && !this.isBanned) {
            this.isBanned = true;
            this.bannedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

}