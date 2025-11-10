package com.oxam.klume.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatCreateRequest {
    private String content;  // 첫 메시지 내용
}
