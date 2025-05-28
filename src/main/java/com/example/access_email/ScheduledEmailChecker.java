package com.example.access_email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledEmailChecker {

    @Autowired
    private EmailReaderService emailReaderService;

    @Scheduled(fixedDelay = 300000) // every 5 minutes
    public void checkForJobEmails() {
        try {
            emailReaderService.fetchJobRelatedEmails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}