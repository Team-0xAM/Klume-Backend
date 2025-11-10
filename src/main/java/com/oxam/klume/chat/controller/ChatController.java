package com.oxam.klume.chat.controller;


import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.dto.MessageRequestDTO;
import com.oxam.klume.chat.dto.MessageResponseDTO;
import com.oxam.klume.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

/* @RestController 아닌 이유
*   웹소켓 컨트롤러는 ResponseBody가 필요없음.
*   rest응답이 없기때문(브로커로 publish된다. 애초에 HTTP 통신이 아님.)
* */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")  // /app/chat 엔드포인트로 publish 하면 이쪽으로 오게한다.
    public void sendMessage(MessageRequestDTO requestDTO, Principal principal) {

        // 🔹 principal 에서 이메일 추출 (JwtChannelInterceptor 에서 넣어줌)
        String senderId = principal != null ? principal.getName() : "anonymous";

        // DB에 저장할 document 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(requestDTO.getRoomId())
                .senderId(senderId)
                .admin(requestDTO.isAdmin())
                .content(requestDTO.getContent())
                .createdAt(LocalDateTime.now().toString())
                .build();

        // DB에 저장 (MongoDB)
        ChatMessage saved = chatMessageRepository.save(chatMessage);

        // 응답 DTO
        MessageResponseDTO responseDTO = new MessageResponseDTO();
        responseDTO.updateSenderId(saved.getSenderId());
        responseDTO.updateContent(saved.getContent());
        responseDTO.updateCreatedAt(saved.getCreatedAt());

        // WebSocket 구독자들에게 전송
        messagingTemplate.convertAndSend("/topic/chat", responseDTO);
    }
}

