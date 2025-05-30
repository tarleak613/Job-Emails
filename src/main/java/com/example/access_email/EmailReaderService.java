package com.example.access_email;

import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class EmailReaderService {
    @Autowired
    private WhatsAppService whatsAppService;

    @Autowired
    private GeminiApiService geminiApiService;

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    private final Set<String> notifiedEmailIds = new HashSet<>();

    private String getEmailUniqueId(Message message) throws MessagingException {
        String[] messageIdHeader = message.getHeader("Message-ID");
        if (messageIdHeader != null && messageIdHeader.length > 0) {
            return messageIdHeader[0];
        }
        // fallback: combine subject+date
        return message.getSubject() + "_" + message.getReceivedDate().getTime();
    }

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

//    public List<Map<String, String>> fetchJobRelatedEmails() throws Exception {
//        List<Map<String, String>> allEmails = fetchEmails();
//        List<Map<String, String>> jobEmails = new ArrayList<>();
//
//        List<String> keywords = Arrays.asList(
//                "job", "jobs", "career", "hiring", "intern", "internship", "placement", "opportunity"
//        );
//
//        for (Map<String, String> email : allEmails) {
//            String subject = email.get("subject").toLowerCase();
//            String body = email.get("body").toLowerCase();
//
//            boolean matches = keywords.stream().anyMatch(
//                    keyword -> subject.contains(keyword) || body.contains(keyword)
//            );
//
//            if (matches) {
//                jobEmails.add(email);
//                //send WhatsApp notification
//                String message = "New job Alert: \n " + subject + email.get("from") + "\nbody:\n" + email.get("body").toLowerCase();
//                whatsAppService.sendWhatsAppMessage(message);
//            }
//        }
//
//        return jobEmails;
//    }
public List<Map<String, String>> fetchJobRelatedEmails() throws Exception {
    List<Map<String, String>> jobEmails = new ArrayList<>();

    Properties properties = new Properties();
    properties.put("mail.store.protocol", "imaps");

    Session session = Session.getDefaultInstance(properties, null);
    Store store = session.getStore("imaps");
    store.connect("imap.gmail.com", username, password);

    Folder inbox = store.getFolder("INBOX");
    inbox.open(Folder.READ_ONLY);

    Flags seen = new Flags(Flags.Flag.SEEN);
    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

    // 1. Filter Unread Emails
    Message[] messages = inbox.search(unseenFlagTerm);

    // 2. Filter Emails from last 15 days
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -15);
    Date fifteenDaysAgo = cal.getTime();

    for (Message message : messages) {
//        if (message.getReceivedDate() != null && message.getReceivedDate().after(fifteenDaysAgo)) {
//            String subject = message.getSubject() != null ? message.getSubject() : "";
//            String body = getTextFromMessage(message);
//
//            List<String> keywords = Arrays.asList(
//                    "job", "jobs", "career", "hiring", "intern", "internship", "placement", "opportunity"
//            );
//
//            boolean matches = keywords.stream().anyMatch(
//                    keyword -> subject.toLowerCase().contains(keyword) || body.toLowerCase().contains(keyword)
//            );
//
//            if (matches) {
//                Map<String, String> emailData = new HashMap<>();
//                emailData.put("from", InternetAddress.toString(message.getFrom()));
//                emailData.put("to", InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
//                emailData.put("subject", subject);
//                emailData.put("body", body);
//                jobEmails.add(emailData);
//
//                // Optional: Avoid duplicates using message ID or subject+date
//                String msg = "New Job Alert:\nFrom: " + emailData.get("from") +
//                        "\nSubject: " + subject + "\nBody:\n" + body;
//                whatsAppService.sendWhatsAppMessage(msg);
//            }
//        }
//
        if (message.getReceivedDate() != null && message.getReceivedDate().after(fifteenDaysAgo)) {
            String subject = message.getSubject() != null ? message.getSubject() : "";
            String body = getTextFromMessage(message);

            System.out.println("Checking email: " + subject);
            System.out.println("Gemini classification: " + geminiApiService.isJobRelated(subject, body));

            if (geminiApiService.isJobRelated(subject, body)) {
                Map<String, String> emailData = new HashMap<>();
                emailData.put("from", InternetAddress.toString(message.getFrom()));
                emailData.put("to", InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
                emailData.put("subject", subject);
                emailData.put("body", body);
                jobEmails.add(emailData);

                String msg = "New Job Alert:\nFrom: " + emailData.get("from") +
                        "\nSubject: " + subject + "\nBody:\n" + body;
                String emailId = getEmailUniqueId(message);
                if (!notifiedEmailIds.contains(emailId)) {
                    // send WhatsApp notification
                    whatsAppService.sendWhatsAppMessage(msg);
                    notifiedEmailIds.add(emailId);
                }
            }
        }
    }

        inbox.close(false);
        store.close();

        return jobEmails;
    }
}
