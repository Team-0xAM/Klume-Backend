package com.oxam.klume.chat.service;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.dto.ChatListDTO;

import java.util.List;

/**
 * 관리자용 채팅 서비스
 */
public interface AdminChatService {
    /**
     * 채팅방 목록 조회 (관리자용)
     * @param organizationId 조직 ID
     * @param userEmail 관리자 이메일
     * @return 채팅방 목록
     */
    List<ChatListDTO> getChatRooms(int organizationId, String userEmail);

    /**
     * 채팅방 담당하기 (관리자용)
     * @param roomId 채팅방 ID
     * @param userEmail 관리자 이메일
     */
    void assignChatRoom(int roomId, String userEmail);

    /**
     * 채팅방 담당 해제 (관리자용)
     * @param roomId 채팅방 ID
     * @param userEmail 관리자 이메일
     */
    void unassignChatRoom(int roomId, String userEmail);

    /**
     * 채팅 히스토리 조회
     * @param roomId 채팅방 ID
     * @param userEmail 사용자 이메일
     * @return 메시지 목록
     */
    List<ChatMessage> getChatHistory(int roomId, String userEmail);
}
