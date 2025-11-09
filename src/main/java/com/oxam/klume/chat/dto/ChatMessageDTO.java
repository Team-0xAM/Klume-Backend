package com.oxam.klume.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessageDTO {
    private String roomId;      // 채팅방 번호
    private String senderId;    // 누가 보냈는지
    private boolean admin;      //  관리자인지 여부
    private String content;     // 메시지 내용
    private String createdAt;   // 보낸 시각

    // 채팅방 번호 설정
    public void updateRoomId(String roomId) {
        this.roomId = roomId;
    }

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
