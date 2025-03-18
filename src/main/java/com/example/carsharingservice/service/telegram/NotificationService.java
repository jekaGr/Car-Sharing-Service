package com.example.carsharingservice.service.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final CarSharingBot carSharingBot;
    @Value("${telegram.bot.chat.id}")
    private String chatId;

    @Autowired
    public NotificationService(CarSharingBot carSharingBot) {
        this.carSharingBot = carSharingBot;
    }

    public void sendNotification(String message) {
        carSharingBot.sendMessage(Long.parseLong(chatId), message);
    }
}
