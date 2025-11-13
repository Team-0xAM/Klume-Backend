package com.oxam.klume.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageResponseDTO {

    private String senderId;
    private String senderName;  // 보낸 사람 닉네임
    private boolean admin;
    private String content;
    private String imageUrl;  // 이미지 URL (옵션)
    private String createdAt;

    // 발신자 ID 설정
    public void updateSenderId(String senderId) {
        this.senderId = senderId;
    }

    // 발신자 닉네임 설정
    public void updateSenderName(String senderName) {
        this.senderName = senderName;
    }

    // 관리자 여부 설정
    public void updateAdmin(boolean admin) {
        this.admin = admin;
    }

    // 메시지 내용 설정
    public void updateContent(String content) {
        this.content = content;
    }

    // 이미지 URL 설정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 생성 시각 설정
    public void updateCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
