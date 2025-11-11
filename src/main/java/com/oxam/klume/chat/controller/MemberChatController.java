package com.oxam.klume.chat.controller;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;
import com.oxam.klume.chat.dto.ChatCreateRequest;
import com.oxam.klume.chat.service.MemberChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원 채팅 API", description = "회원 채팅 API")
@RequestMapping("/my-chats")
@RequiredArgsConstructor
@RestController
public class MemberChatController {
    private final MemberChatService memberChatService;

    @Operation(summary = "특정 조직의 내 채팅방 조회 또는 생성",
            description = "채팅방이 있으면 조회, 없으면 첫 메시지와 함께 생성")
    @PostMapping("/organizations/{organizationId}")
    public ResponseEntity<ChatRoom> getOrCreateMyChatRoom(
            @PathVariable int organizationId,
            @RequestBody ChatCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ChatRoom chatRoom = memberChatService.getOrCreateMyChatRoom(organizationId, userEmail, request.getContent());
        return ResponseEntity.ok(chatRoom);
    }

    @Operation(summary = "채팅방 메시지 조회(회원)")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable int roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<ChatMessage> messages = memberChatService.getMyChatMessages(roomId, userEmail);
        return ResponseEntity.ok(messages);
    }
}
