package com.oxam.klume.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatCreateResponse {
    private String roomId;
    private String message;
}
