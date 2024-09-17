package com.mail_scanner;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRepository emailRepository;

    public List<Email> fetchEmails() {
        List<Email> emails = new ArrayList<>();

        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            LOGGER.log(Level.INFO, "Connecting to IMAP server...");

//            String username = "";
//            String password = "";
//            store.connect("imap.gmail.com", username, password);

            store.connect("imap.gmail.com", System.getenv("EMAIL_USERNAME"), System.getenv("EMAIL_PASSWORD"));
            LOGGER.log(Level.INFO, "Connected to IMAP server.");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            LOGGER.log(Level.INFO, "Number of messages in inbox: " + messages.length);
            for (Message message : messages) {
                Email email = new Email();
                email.setSender(((InternetAddress) message.getFrom()[0]).getAddress());
                email.setSubject(message.getSubject());
                email.setContent(message.getContent().toString());
                email.setReceivedDate(message.getReceivedDate());
                emails.add(email);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching emails", e);
        }

        emailRepository.saveAll(emails);
        return emails;
    }
}



