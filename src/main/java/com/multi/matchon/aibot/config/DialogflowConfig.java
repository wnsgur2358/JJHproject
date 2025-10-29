package com.multi.matchon.aibot.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class DialogflowConfig {

    @Value("${matchon.dialogflow-key-path}")
    private String dialogflowKeyPath;

    @Bean
    public SessionsClient sessionsClient() throws IOException {
        // 파일 경로에서 FileInputStream 열기
        FileInputStream serviceAccountStream = new FileInputStream(dialogflowKeyPath);

        // 구글 인증 객체 생성
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);

        // 세션 설정에 인증 객체 적용
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return SessionsClient.create(sessionsSettings);
    }
}
