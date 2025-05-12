package com.samyookgoo.palgoosam.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmInitialization {

    @PostConstruct
    public void init() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("./src/main/resources/firebase-admin-sdk.json");
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(
                    GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
