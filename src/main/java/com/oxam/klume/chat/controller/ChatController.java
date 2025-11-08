package com.oxam.klume.chat.controller;


import com.oxam.klume.chat.dto.MessageRequestDTO;
import com.oxam.klume.chat.dto.MessageResponseDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/* @RestController 아닌 이유
*   웹소켓 컨트롤러는 ResponseBody가 필요없음. rest응답이 없기때문(브로커로 publish된다. 애초에 HTTP 통신이 아님.)
* */
@Controller
public class ChatController {

    @MessageMapping("/chat")  // "/app/chat" 엔드포인트로 publish 하면 이쪽으로 오게한다.
    @SendTo("/topic/chat")    // 받아서 브로커에게 넘겨준다.

    public MessageResponseDTO sendMessage(MessageRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = new MessageResponseDTO();
        responseDTO.setContent(requestDTO.getContent());
        return responseDTO;
    }
}
