package com.ledahl.apps.recieppyapi.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.io.IOException

@Configuration
class FirebaseConfiguration {
    @Bean
    @Throws(IOException::class)
    fun firebaseAuth(): FirebaseAuth {
        val serviceAccount = FileInputStream("recieppy-firebase-adminsdk-h7dpd-c18a54383d.json")
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://recieppy.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)
        return FirebaseAuth.getInstance()
    }
}