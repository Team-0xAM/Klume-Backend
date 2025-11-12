package com.oxam.klume.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageResponseDTO {

    private String senderId;
    private boolean admin;
    private String content;
    private String createdAt;

    // 발신자 ID 설정
    public void updateSenderId(String senderId) {
        this.senderId = senderId;
    }

    // 관리자 여부 설정
    public void updateAdmin(boolean admin) {
        this.admin = admin;
    }

    // 메시지 내용 설정
    public void updateContent(String content) {
        this.content = content;
    }

    // 생성 시각 설정
    public void updateCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
