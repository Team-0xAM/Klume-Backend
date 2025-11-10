package com.oxam.klume.chat.service;



import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.dto.ChatCreateRequest;
import com.oxam.klume.chat.dto.ChatCreateResponse;
import com.oxam.klume.chat.dto.ChatListDTO;

import java.util.List;

public interface ChatService {
    // 채팅방 목록 조회 (관리자용)
    List<ChatListDTO> getChatRooms(int organizationId, String userEmail);

    // 채팅방 생성 (일반 회원용)
    ChatCreateResponse createChatRoom(int organizationId, String userEmail, ChatCreateRequest request);

    // 채팅방 담당하기 (관리자용) - roomId로
    void assignChatRoom(int roomId, String userEmail);

    // 채팅방 담당 해제 (관리자용) - roomId로
    void unassignChatRoom(int roomId, String userEmail);

    // 채팅 히스토리 조회
    List<ChatMessage> getChatHistory(int roomId, String userEmail);
}
