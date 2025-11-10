package com.oxam.klume.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;

    private String roomId;           // UUID
    private int organizationId;      // 어느 조직의 채팅인지
    private int createdById;         // 채팅 시작한 일반 회원 ID
    private String createdByEmail;   // 채팅 시작한 회원 이메일
    private Integer assignedToId;    // 담당 관리자 OrganizationMember ID (null이면 미배정)
    private String assignedToName;   // 담당 관리자 닉네임
    private String createdAt;
    private String lastMessageAt;    // 마지막 메시지 시각 (정렬용)

    // 채팅방 생성 (일반 회원이 관리자에게 문의 시작)
    public static ChatRoom create(int organizationId, int createdById, String createdByEmail) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .organizationId(organizationId)
                .createdById(createdById)
                .createdByEmail(createdByEmail)
                .assignedToId(null)
                .assignedToName(null)
                .createdAt(now)
                .lastMessageAt(now)
                .build();
    }

    // 담당자 지정 (관리자가 "이 문의 담당하기" 클릭)
    public void assignTo(int adminId, String adminName) {
        this.assignedToId = adminId;
        this.assignedToName = adminName;
    }

    // 담당 해제
    public void unassign() {
        this.assignedToId = null;
        this.assignedToName = null;
    }

    // 마지막 메시지 시각 업데이트
    public void updateLastMessageTime() {
        this.lastMessageAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
