package com.oxam.klume.faq.entity;

import com.oxam.klume.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "faq")
@NoArgsConstructor
@Entity
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "question_title", nullable = false)
    private String questionTitle;

    @Column(name = "question_content", nullable = false)
    private String questionContent;

    @Column(nullable = false)
    private String answer;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}