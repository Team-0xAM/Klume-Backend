package com.oxam.klume.chat.repository;

import com.oxam.klume.chat.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<ChatRoom, String> {
    // 특정 조직의 모든 채팅방 조회 (관리자용)
    List<ChatRoom> findByOrganizationIdOrderByLastMessageAtDesc(int organizationId);

    // roomId로 채팅방 조회
    Optional<ChatRoom> findByRoomId(int roomId);

    // 특정 회원이 특정 조직에 생성한 채팅방 조회 (일반 회원용)
    Optional<ChatRoom> findByOrganizationIdAndCreatedById(int organizationId, int memberId);

    // 담당자가 배정된 채팅방 조회
    List<ChatRoom> findByAssignedToIdOrderByLastMessageAtDesc(int assignedToId);

    // 미배정 채팅방 조회 (assignedToId가 null인 것들)
    List<ChatRoom> findByOrganizationIdAndAssignedToIdIsNullOrderByLastMessageAtDesc(int organizationId);
}
