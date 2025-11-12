package com.oxam.klume.chat.controller;


import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;
import com.oxam.klume.chat.dto.MessageRequestDTO;
import com.oxam.klume.chat.dto.MessageResponseDTO;
import com.oxam.klume.chat.repository.ChatMessageRepository;
import com.oxam.klume.chat.repository.ChatRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* @RestController 아닌 이유
*   웹소켓 컨트롤러는 ResponseBody가 필요없음.
*   rest응답이 없기때문(브로커로 publish된다. 애초에 HTTP 통신이 아님.)
* */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")  // /app/chat 엔드포인트로 publish 하면 이쪽으로 오게한다.
    public void sendMessage(MessageRequestDTO requestDTO, Principal principal) {

        // principal 에서 이메일 추출 (JwtChannelInterceptor 에서 넣어줌)
        String senderEmail = principal != null ? principal.getName() : "anonymous";

        // 채팅방 조회
        ChatRoom chatRoom = chatRepository.findByRoomId(requestDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 권한 검증: 메시지를 보낼 수 있는지 확인
        Member sender = memberRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        boolean canSend = canSendMessage(chatRoom, sender, requestDTO.isAdmin());
        if (!canSend) {
            throw new RuntimeException("메시지를 보낼 권한이 없습니다.");
        }

        // DB에 저장할 document 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(requestDTO.getRoomId())
                .senderId(senderEmail)
                .admin(requestDTO.isAdmin())
                .content(requestDTO.getContent())
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        // DB에 저장 (MongoDB)
        ChatMessage saved = chatMessageRepository.save(chatMessage);

        // ChatRoom의 lastMessageAt 업데이트
        chatRoom.updateLastMessageTime();
        chatRepository.save(chatRoom);

        // 응답 DTO
        MessageResponseDTO responseDTO = new MessageResponseDTO();
        responseDTO.updateSenderId(saved.getSenderId());
        responseDTO.updateContent(saved.getContent());
        responseDTO.updateCreatedAt(saved.getCreatedAt());

        // 특정 채팅방 구독자들에게만 전송 (채널 분리)
        messagingTemplate.convertAndSend("/topic/chat-room/" + requestDTO.getRoomId(), responseDTO);
    }

    /**
     * 메시지 전송 권한 확인
     * - 일반 회원: 자기가 만든 채팅방에만 보낼 수 있음
     * - 관리자: 자기가 담당한 채팅방에만 보낼 수 있음
     */
    private boolean canSendMessage(ChatRoom chatRoom, Member sender, boolean isAdmin) {
        // 일반 회원인 경우: 자기가 만든 채팅방인지 확인
        if (!isAdmin) {
            return chatRoom.getCreatedById() == sender.getId();
        }

        // 관리자인 경우: 담당자로 지정되어 있는지 확인
        if (chatRoom.getAssignedToId() == null) {
            return false; // 담당자가 없으면 답장 불가
        }

        // 담당자 정보 조회하여 member ID 확인
        OrganizationMember assignedMember = organizationMemberRepository
            .findById(chatRoom.getAssignedToId())
            .orElse(null);

        if (assignedMember == null) {
            return false;
        }

        return assignedMember.getMember().getId() == sender.getId();
    }
}

