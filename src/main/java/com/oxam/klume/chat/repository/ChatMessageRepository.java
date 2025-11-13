package com.oxam.klume.chat.repository;

import com.oxam.klume.chat.document.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// MongoDB의 ChatMessage 컬렉션에 접근하는 Repository (상속)

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // 특정 조직의 특정 방(organizationId + roomId)의 메시지를 생성 시간(createdAt) 순으로 조회
    List<ChatMessage> findByOrganizationIdAndRoomIdOrderByCreatedAtAsc(int organizationId, int roomId);

    // 하위 호환성을 위한 기존 메서드 (deprecated)
    @Deprecated
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(int roomId);
}