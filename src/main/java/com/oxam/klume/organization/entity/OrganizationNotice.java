package com.oxam.klume.organization.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "organization_notice")
@NoArgsConstructor
@Entity
public class OrganizationNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @JoinColumn(name = "organization_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @JoinColumn(name = "organization_member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationMember organizationMember;
}