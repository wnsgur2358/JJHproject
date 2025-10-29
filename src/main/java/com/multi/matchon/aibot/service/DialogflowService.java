package com.multi.matchon.aibot.service;

import com.google.cloud.dialogflow.v2.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DialogflowService {

    private final SessionsClient sessionsClient;
    private static final String PROJECT_ID = "matchon-chatbot";

    public String detectIntent(String sessionId, String message) {
        try {
            SessionName session = SessionName.of(PROJECT_ID, sessionId);
            TextInput textInput = TextInput.newBuilder().setText(message).setLanguageCode("ko").build();
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(request);
            return response.getQueryResult().getFulfillmentText();
        } catch (Exception e) {
            return "에러가 발생했습니다: " + e.getMessage();
        }
    }

}
