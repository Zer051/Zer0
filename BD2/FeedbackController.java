package com.example.feedback.controller;

import com.example.feedback.model.Feedback;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class FeedbackController {

    private static final String FEEDBACK_FILE = "feedback_log.txt";

    @PostMapping("/api/feedback")
    public ResponseEntity<String> submitFeedback(@RequestBody Feedback feedback) {
        if (feedback.getName() == null || feedback.getName().isEmpty() ||
                feedback.getEmail() == null || feedback.getEmail().isEmpty() ||
                feedback.getComment() == null || feedback.getComment().isEmpty()) {
            return ResponseEntity.badRequest().body("Ошибка: все поля должны быть заполнены.");
        }

        if (!isValidEmail(feedback.getEmail())) {
            return ResponseEntity.badRequest().body("Ошибка: введите корректный email.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FEEDBACK_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("[%s] %s (%s): %s%n",
                    timestamp,
                    feedback.getName(),
                    feedback.getEmail(),
                    feedback.getComment()));

            return ResponseEntity.ok(String.format(
                    "Спасибо, %s! Ваш комментарий принят.", feedback.getName()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Ошибка при сохранении отзыва.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}