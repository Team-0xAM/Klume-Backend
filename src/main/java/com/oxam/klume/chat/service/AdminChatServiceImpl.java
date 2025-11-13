package com.oxam.klume.chat.service;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;
import com.oxam.klume.chat.dto.ChatListDTO;
import com.oxam.klume.chat.repository.ChatMessageRepository;
import com.oxam.klume.chat.repository.ChatRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminChatServiceImpl implements AdminChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    /**
     * 채팅방 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    @Override
    public List<ChatListDTO> getChatRooms(int organizationId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        OrganizationMember orgMember = organizationMemberRepository
            .findByOrganizationIdAndMemberId(organizationId, member.getId())
            .orElseThrow(() -> new RuntimeException("조직 멤버를 찾을 수 없습니다."));

        if (orgMember.getRole() != OrganizationRole.ADMIN) {
            throw new RuntimeException("관리자만 조회할 수 있습니다.");
        }

        List<ChatRoom> chatRooms = chatRepository.findByOrganizationIdOrderByLastMessageAtDesc(organizationId);

        return chatRooms.stream()
            .map(ChatListDTO::from)
            .toList();
    }

    /**
     * 채팅방 담당하기 (관리자용)
     */
    @Transactional
    @Override
    public void assignChatRoom(int roomId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        OrganizationMember orgMember = organizationMemberRepository
            .findByOrganizationIdAndMemberId(chatRoom.getOrganizationId(), member.getId())
            .orElseThrow(() -> new RuntimeException("조직 멤버를 찾을 수 없습니다."));

        if (orgMember.getRole() != OrganizationRole.ADMIN) {
            throw new RuntimeException("관리자만 담당할 수 있습니다.");
        }

        chatRoom.assignTo(orgMember.getId(), orgMember.getNickname(), userEmail);
        chatRepository.save(chatRoom);

        // 시스템 메시지: 담당자 배정
        ChatMessage systemMessage = ChatMessage.builder()
            .organizationId(chatRoom.getOrganizationId())
            .roomId(chatRoom.getRoomId())
            .senderId("SYSTEM")
            .senderName("시스템")
            .admin(false)
            .content(orgMember.getNickname() + "(가) 배정되었습니다.")
            .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
        chatMessageRepository.save(systemMessage);
    }

    /**
     * 채팅방 담당 해제 (관리자용)
     */
    @Transactional
    @Override
    public void unassignChatRoom(int roomId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        if (chatRoom.getAssignedToId() == null) {
            throw new RuntimeException("담당자가 지정되지 않은 채팅방입니다.");
        }

        OrganizationMember orgMember = organizationMemberRepository.findById(chatRoom.getAssignedToId())
            .orElseThrow(() -> new RuntimeException("담당자를 찾을 수 없습니다."));

        if (orgMember.getMember().getId() != member.getId()) {
            throw new RuntimeException("담당자만 해제할 수 있습니다.");
        }

        String assignedName = orgMember.getNickname();
        chatRoom.unassign();
        chatRepository.save(chatRoom);

        // 시스템 메시지: 담당 해제
        ChatMessage systemMessage = ChatMessage.builder()
            .organizationId(chatRoom.getOrganizationId())
            .roomId(chatRoom.getRoomId())
            .senderId("SYSTEM")
            .senderName("시스템")
            .admin(false)
            .content(assignedName + "(가) 배정 해제되었습니다.")
            .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
        chatMessageRepository.save(systemMessage);
    }

    /**
     * 채팅 히스토리 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<ChatMessage> getChatHistory(int roomId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        boolean canView = canViewChatRoom(chatRoom, member);
        if (!canView) {
            throw new RuntimeException("채팅방을 조회할 권한이 없습니다.");
        }

        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    /**
     * 채팅방 조회 권한 확인
     */
    private boolean canViewChatRoom(ChatRoom chatRoom, Member member) {
        if (chatRoom.getCreatedById() == member.getId()) {
            return true;
        }

        OrganizationMember orgMember = organizationMemberRepository
            .findByOrganizationIdAndMemberId(chatRoom.getOrganizationId(), member.getId())
            .orElse(null);

        return orgMember != null && orgMember.getRole() == OrganizationRole.ADMIN;
    }
}
