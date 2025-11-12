package com.oxam.klume.organization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "organization_group")
@NoArgsConstructor
@Entity
public class OrganizationGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @JoinColumn(name = "organization_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    public OrganizationGroup(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public void updateOrganization(final Organization organization) {
        this.organization = organization;
    }

    public void updateOrganizationGroup(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}