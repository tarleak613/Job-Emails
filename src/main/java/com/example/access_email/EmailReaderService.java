package com.example.access_email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import java.util.*;

@Service
public class EmailReaderService {
    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    public List<Map<String, String>> fetchEmails() throws Exception {
        List<Map<String, String>> emails = new ArrayList<>();

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(properties, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();

        for (Message message : messages) {
            Map<String, String> emailData = new HashMap<>();
            emailData.put("from", InternetAddress.toString(message.getFrom()));
            emailData.put("to", InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
            emailData.put("subject", message.getSubject());
            emailData.put("body", getTextFromMessage(message));
            emails.add(emailData);
        }

        inbox.close(false);
        store.close();

        return emails;
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            }
        }
        return result.toString();
    }

    public List<Map<String, String>> fetchJobRelatedEmails() throws Exception {
        List<Map<String, String>> allEmails = fetchEmails();
        List<Map<String, String>> jobEmails = new ArrayList<>();

        List<String> keywords = Arrays.asList(
                "job", "jobs", "career", "hiring", "intern", "internship", "placement", "opportunity"
        );

        for (Map<String, String> email : allEmails) {
            String subject = email.get("subject").toLowerCase();
            String body = email.get("body").toLowerCase();

            boolean matches = keywords.stream().anyMatch(
                    keyword -> subject.contains(keyword) || body.contains(keyword)
            );

            if (matches) {
                jobEmails.add(email);
            }
        }

        return jobEmails;
    }

}
