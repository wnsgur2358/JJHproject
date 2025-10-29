package com.multi.matchon.aibot.controller;

import com.multi.matchon.aibot.service.DialogflowService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AibotController {

    private final DialogflowService dialogflowService;

    @PostMapping("/api/aichat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> payload, HttpSession session) {
        String message = payload.get("message");
        String sessionId = session.getId();
        String responseText = dialogflowService.detectIntent(sessionId, message);

        Map<String, Object> response = new HashMap<>();
        response.put("reply", responseText);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aichat")
    public String chatPage() {
        return "aibot/aichatbot";
    }
}
