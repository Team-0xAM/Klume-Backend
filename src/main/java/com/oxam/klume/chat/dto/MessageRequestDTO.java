package com.oxam.klume.chat.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageRequestDTO {
    private String roomId;   // 어떤 채팅방인지
    private String content;  // 메시지 내용
    private boolean admin;   // 관리자인지 여부

}
