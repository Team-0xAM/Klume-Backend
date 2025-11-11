package com.oxam.klume.chat.controller;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.dto.ChatListDTO;
import com.oxam.klume.chat.service.AdminChatService;
import com.oxam.klume.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자용 채팅 관련 API", description = "채팅방 관련 API (관리자용)")
@RequestMapping("/organizations/{organizationId}/chat-rooms")
@RequiredArgsConstructor
@RestController
public class AdminChatController {
    private final AdminChatService adminChatService;
    private final ChatService chatService;  // 기존 createChatRoom용 (일반 회원)

    @Operation(summary = "채팅방 목록 조회 (관리자용)")
    @GetMapping
    public ResponseEntity<List<ChatListDTO>> getChatRooms(@PathVariable int organizationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<ChatListDTO> chatRooms = adminChatService.getChatRooms(organizationId, userEmail);
        return ResponseEntity.ok(chatRooms);
    }


    @Operation(summary = "채팅방 담당하기 (관리자용)")
    @PostMapping("/{roomId}/assign")
    public ResponseEntity<Void> assignChatRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        adminChatService.assignChatRoom(roomId, userEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 담당 해제 (관리자용)")
    @DeleteMapping("/{roomId}/assign")
    public ResponseEntity<Void> unassignChatRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        adminChatService.unassignChatRoom(roomId, userEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅 히스토리 조회(관리자)")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<ChatMessage> messages = adminChatService.getChatHistory(roomId, userEmail);
        return ResponseEntity.ok(messages);
    }
}
