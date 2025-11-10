package com.oxam.klume.chat.service;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;
import com.oxam.klume.chat.dto.ChatCreateRequest;
import com.oxam.klume.chat.dto.ChatCreateResponse;
import com.oxam.klume.chat.dto.ChatListDTO;
import com.oxam.klume.chat.repository.ChatMessageRepository;
import com.oxam.klume.chat.repository.ChatRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 목록 조회 (관리자용)
    @Override
    public List<ChatListDTO> getChatRooms(int organizationId, String userEmail) {
        // 권한 확인: 관리자만 조회 가능
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        OrganizationMember orgMember = organizationMemberRepository
            .findByOrganizationIdAndMemberId(organizationId, member.getId())
            .orElseThrow(() -> new RuntimeException("조직 멤버를 찾을 수 없습니다."));

        if (orgMember.getRole() != OrganizationRole.ADMIN) {
            throw new RuntimeException("관리자만 조회할 수 있습니다.");
        }

        // 해당 조직의 모든 채팅방 조회
        List<ChatRoom> chatRooms = chatRepository.findByOrganizationIdOrderByLastMessageAtDesc(organizationId);

        return chatRooms.stream()
            .map(ChatListDTO::from)
            .toList();
    }

    // 채팅방 생성 (일반 회원용)
    @Override
    public ChatCreateResponse createChatRoom(int organizationId, String userEmail, ChatCreateRequest request) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

        // 이미 생성된 채팅방이 있는지 확인
        var existingChatRoom = chatRepository.findByOrganizationIdAndCreatedById(organizationId, member.getId());
        if (existingChatRoom.isPresent()) {
            ChatRoom chatRoom = existingChatRoom.get();
            return new ChatCreateResponse(
                chatRoom.getRoomId(),
                "이미 생성된 채팅방이 있습니다."
            );
        }

        // 새 채팅방 생성
        ChatRoom chatRoom = ChatRoom.create(organizationId, member.getId(), userEmail);
        chatRepository.save(chatRoom);

        // 첫 메시지 저장 (MongoDB)
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            ChatMessage chatMessage = ChatMessage.builder()
                .roomId(chatRoom.getRoomId())
                .senderId(userEmail)
                .admin(false)
                .content(request.getContent())
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
            chatMessageRepository.save(chatMessage);
        }

        return new ChatCreateResponse(
            chatRoom.getRoomId(),
            "채팅방이 생성되었습니다."
        );
    }

    // 채팅방 담당하기 (관리자용)
    @Override
    public void assignChatRoom(int chatRoomId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRepository.findById(String.valueOf(chatRoomId))
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 권한 확인: 해당 조직의 관리자인지 확인
        OrganizationMember orgMember = organizationMemberRepository
            .findByOrganizationIdAndMemberId(chatRoom.getOrganizationId(), member.getId())
            .orElseThrow(() -> new RuntimeException("조직 멤버를 찾을 수 없습니다."));

        if (orgMember.getRole() != OrganizationRole.ADMIN) {
            throw new RuntimeException("관리자만 담당할 수 있습니다.");
        }

        // 담당자 지정
        chatRoom.assignTo(orgMember.getId(), orgMember.getNickname());
        chatRepository.save(chatRoom);
    }

    // 채팅방 담당 해제 (관리자용)
    @Override
    public void unassignChatRoom(int chatRoomId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRepository.findById(String.valueOf(chatRoomId))
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 권한 확인: 현재 담당자인지 확인
        if (chatRoom.getAssignedToId() == null) {
            throw new RuntimeException("담당자가 지정되지 않은 채팅방입니다.");
        }

        OrganizationMember orgMember = organizationMemberRepository.findById(chatRoom.getAssignedToId())
            .orElseThrow(() -> new RuntimeException("담당자를 찾을 수 없습니다."));

        if (orgMember.getMember().getId() != member.getId()) {
            throw new RuntimeException("담당자만 해제할 수 있습니다.");
        }

        // 담당 해제
        chatRoom.unassign();
        chatRepository.save(chatRoom);
    }
}
