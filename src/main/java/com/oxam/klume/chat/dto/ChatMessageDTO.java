package com.oxam.klume.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessage {
    private String roomId;      // 채팅방 번호
    private String senderId;    // 누가 보냈는지
    private boolean admin;      //  관리자인지 여부
    private String content;     // 메시지 내용
    private String createdAt;   // 보낸 시각
}
