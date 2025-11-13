package com.oxam.klume.chat.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_messages") // MongoDB 컬렉션 이름
public class ChatMessage {

    @Id
    private String id;

    private int roomId;         // 채팅방 번호 (auto-increment)
    private String senderId;    // 누가 보냈는지
    private boolean admin;      //  관리자인지 여부
    private String content;     // 메시지 내용
    private String imageUrl;    // 이미지 URL (옵션)
    private String createdAt;   // 보낸 시각

    // 채팅방 번호 설정
    public void updateRoomId(int roomId) {
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

    // 이미지 URL 설정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 생성 시각 설정
    public void updateCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
