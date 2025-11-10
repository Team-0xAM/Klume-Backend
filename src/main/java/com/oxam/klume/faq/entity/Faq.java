package com.oxam.klume.faq.entity;

import com.oxam.klume.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static Faq create(
            String title,
            String content,
            String answer,
            Member member
    ) {
        Faq faq = new Faq();
        faq.questionTitle = title;
        faq.questionContent = content;
        faq.answer = answer;
        faq.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        faq.updatedAt = null;
        faq.member = member;
        return faq;
    }

    public void update(String title, String content, String answer, Member member) {
        this.questionTitle = title;
        this.questionContent = content;
        this.answer = answer;
        this.member = member;
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
