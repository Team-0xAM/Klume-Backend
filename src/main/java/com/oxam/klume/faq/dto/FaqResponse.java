package com.oxam.klume.faq.dto;

import com.oxam.klume.faq.entity.Faq;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaqResponse {
    private int id;
    private String title;
    private int memberId;
    private String content;
    private String answer;
    private String createdAt;
    private String updatedAt;

    public static FaqResponse of(final Faq faq) {
        return FaqResponse.builder()
                .id(faq.getId())
                .memberId(faq.getMember().getId())
                .title(faq.getQuestionTitle())
                .content(faq.getQuestionContent())
                .answer(faq.getAnswer())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }
}