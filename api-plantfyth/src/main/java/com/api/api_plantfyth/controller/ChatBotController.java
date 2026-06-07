package com.api.api_plantfyth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api.api_plantfyth.service.ChatBotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/chat")
public class ChatBotController {

    @Autowired
    private ChatBotService chatbotService;

    @PostMapping(consumes = "text/plain")
public ResponseEntity<String> chat(@RequestBody String pergunta) {
    return ResponseEntity.ok(chatbotService.chat(pergunta));
}
}
