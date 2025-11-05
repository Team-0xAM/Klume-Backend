package com.oxam.klume.member.entity;

import com.oxam.klume.member.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "system_role")
@NoArgsConstructor
@Entity
public class SystemRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role name;

    @Column(nullable = false)
    private String description;
}