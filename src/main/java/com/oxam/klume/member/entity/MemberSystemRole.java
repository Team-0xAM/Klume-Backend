package com.oxam.klume.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "member_system_role")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MemberSystemRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "system_role_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private SystemRole systemRole;
}