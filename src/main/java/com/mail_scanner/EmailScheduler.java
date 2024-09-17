package com.mail_scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000) // runs every minute
    public void scanEmails() {
        emailService.fetchEmails();
    }
}

