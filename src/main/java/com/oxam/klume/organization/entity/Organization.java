package com.oxam.klume.organization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "organization")
@NoArgsConstructor
@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    public Organization(final String name, final String description, final String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public void updateImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateOrganization(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}