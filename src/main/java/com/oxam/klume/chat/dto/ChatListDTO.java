package com.oxam.klume.chat.dto;

import com.oxam.klume.chat.document.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatListDTO {
    private String id;                  // MongoDB _id
    private int roomId;
    private String createdByEmail;      // 문의자 이메일
    private String assignedToName;      // 담당자 이름 (null이면 "미배정")
    private Integer assignedToId;       // 담당자 ID
    private String lastMessageAt;       // 마지막 메시지 시각
    private String createdAt;

    public static ChatListDTO from(ChatRoom chatRoom) {
        return new ChatListDTO(
            chatRoom.getId(),
            chatRoom.getRoomId(),
            chatRoom.getCreatedByEmail(),
            chatRoom.getAssignedToName(),
            chatRoom.getAssignedToId(),
            chatRoom.getLastMessageAt(),
            chatRoom.getCreatedAt()
        );
    }
}
