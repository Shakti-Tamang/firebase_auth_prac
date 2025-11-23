package com.example.firebase_auth.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Component
public class FirebaseConfig {

    @Value("classpath:firebase.json")
    private Resource privateKey;

    @Value("${firebase.web-api-key:}")
    private String webApiKey;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream credentials = privateKey.getInputStream();
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentials))
                .build();
        return FirebaseApp.initializeApp(firebaseOptions);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    public String getWebApiKey() {
        return webApiKey;
    }
}