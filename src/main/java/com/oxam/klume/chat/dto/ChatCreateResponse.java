package com.oxam.klume.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatCreateResponse {
    private int roomId;
    private String message;
}
