package com.oxam.klume.chat.controller;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.dto.ChatCreateRequest;
import com.oxam.klume.chat.dto.ChatCreateResponse;
import com.oxam.klume.chat.dto.ChatListDTO;
import com.oxam.klume.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "chat_room", description = "채팅방 관련 API")
@RequestMapping("/organizations/{organizationId}/chat-rooms")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatService chatService;

    @Operation(summary = "채팅방 목록 조회 (관리자용)")
    @GetMapping
    public ResponseEntity<List<ChatListDTO>> getChatRooms(@PathVariable int organizationId) {
        String userEmail = getCurrentUserEmail();
        List<ChatListDTO> chatRooms = chatService.getChatRooms(organizationId, userEmail);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "채팅방 생성 (일반 회원용)")
    @PostMapping
    public ResponseEntity<ChatCreateResponse> createChatRoom(
            @PathVariable int organizationId,
            @RequestBody ChatCreateRequest request) {
        String userEmail = getCurrentUserEmail();
        ChatCreateResponse response = chatService.createChatRoom(organizationId, userEmail, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 담당하기 (관리자용)")
    @PostMapping("/{roomId}/assign")
    public ResponseEntity<Void> assignChatRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        String userEmail = getCurrentUserEmail();
        chatService.assignChatRoom(roomId, userEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 담당 해제 (관리자용)")
    @DeleteMapping("/{roomId}/assign")
    public ResponseEntity<Void> unassignChatRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        String userEmail = getCurrentUserEmail();
        chatService.unassignChatRoom(roomId, userEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅 히스토리 조회")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        String userEmail = getCurrentUserEmail();
        List<ChatMessage> messages = chatService.getChatHistory(roomId, userEmail);
        return ResponseEntity.ok(messages);
    }

    // JWT에서 현재 사용자 이메일 가져오기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // JWT에서 설정한 이메일
        }
        throw new RuntimeException("인증되지 않은 사용자입니다.");
    }
}
