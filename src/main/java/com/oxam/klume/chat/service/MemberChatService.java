package com.oxam.klume.chat.service;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;

import java.util.List;

/**
 * 회원용 채팅 서비스
 */
public interface MemberChatService {
    /**
     * 특정 조직의 내 채팅방 조회 또는 생성
     * - 기존 채팅방이 있으면 조회
     * - 없으면 첫 메시지와 함께 새로 생성
     *
     * @param organizationId 조직 ID
     * @param userEmail 회원 이메일
     * @param firstMessage 첫 메시지 (채팅방 생성 시)
     * @return 채팅방 정보
     */
    ChatRoom getOrCreateMyChatRoom(int organizationId, String userEmail, String firstMessage);

    /**
     * 내 채팅방 메시지 조회
     *
     * @param roomId 채팅방 ID
     * @param userEmail 회원 이메일
     * @return 메시지 목록
     */
    List<ChatMessage> getMyChatMessages(int roomId, String userEmail);
}
