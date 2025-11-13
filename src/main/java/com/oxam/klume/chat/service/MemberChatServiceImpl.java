package com.oxam.klume.chat.service;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;
import com.oxam.klume.chat.repository.ChatMessageRepository;
import com.oxam.klume.chat.repository.ChatRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberChatServiceImpl implements MemberChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final SequenceGeneratorService sequenceGenerator;

    /**
     * 특정 조직의 내 채팅방 조회 또는 생성
     */
    @Transactional
    @Override
    public ChatRoom getOrCreateMyChatRoom(int organizationId, String userEmail, String firstMessage) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

        OrganizationMember orgMember = organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, member.getId())
            .orElseThrow(() -> new RuntimeException("조직 멤버가 아닙니다."));

        var existingChatRoom = chatRepository.findByOrganizationIdAndCreatedById(organizationId, member.getId());

        if (existingChatRoom.isPresent()) {
            return existingChatRoom.get();
        }

        int nextRoomId = sequenceGenerator.getNextSequence("chat_room_id");
        ChatRoom chatRoom = ChatRoom.create(nextRoomId, organizationId, member.getId(), orgMember.getNickname(), userEmail);
        chatRepository.save(chatRoom);

        // 시스템 메시지: 채팅 오픈
        ChatMessage systemMessage = ChatMessage.builder()
            .organizationId(organizationId)
            .roomId(chatRoom.getRoomId())
            .senderId("SYSTEM")
            .senderName("시스템")
            .admin(false)
            .content(orgMember.getNickname() + "(가) 채팅을 오픈했습니다.")
            .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
        chatMessageRepository.save(systemMessage);

        if (firstMessage != null && !firstMessage.trim().isEmpty()) {
            ChatMessage chatMessage = ChatMessage.builder()
                .organizationId(organizationId)  // 조직 ID 추가
                .roomId(chatRoom.getRoomId())
                .senderId(userEmail)
                .senderName(orgMember.getNickname())  // 보낸 사람 닉네임 추가
                .admin(false)
                .content(firstMessage)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
            chatMessageRepository.save(chatMessage);

            chatRoom.updateLastMessage(firstMessage);
            chatRepository.save(chatRoom);
        }

        return chatRoom;
    }


    // 내 채팅방 메시지 조회
    @Transactional(readOnly = true)
    @Override
    public List<ChatMessage> getMyChatMessages(int roomId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        if (chatRoom.getCreatedById() != member.getId()) {
            throw new RuntimeException("본인의 채팅방만 조회할 수 있습니다.");
        }

        // organizationId와 roomId를 함께 사용하여 필터링
        return chatMessageRepository.findByOrganizationIdAndRoomIdOrderByCreatedAtAsc(
            chatRoom.getOrganizationId(),
            roomId
        );
    }
}
