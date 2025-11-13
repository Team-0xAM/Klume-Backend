package com.oxam.klume.chat.controller;

import com.oxam.klume.chat.document.ChatMessage;
import com.oxam.klume.chat.document.ChatRoom;
import com.oxam.klume.chat.dto.ChatCreateRequest;
import com.oxam.klume.chat.service.MemberChatService;
import com.oxam.klume.file.infra.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "회원 채팅 API", description = "회원 채팅 API")
@RequestMapping("/my-chats")
@RequiredArgsConstructor
@RestController
public class MemberChatController {
    private final MemberChatService memberChatService;
    private final S3Uploader s3Uploader;

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

    @Operation(summary = "채팅 이미지 업로드", description = "채팅에 첨부할 이미지를 S3에 업로드하고 URL을 반환합니다")
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadChatImage(@RequestParam("image") MultipartFile image) {
        String imageUrl = s3Uploader.upload("chat/", image);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);

        return ResponseEntity.ok(response);
    }
}
