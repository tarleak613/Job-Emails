package com.example.access_email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EmailWriterService {
    @Autowired
    private GeminiApiService geminiApiService;

    @Autowired
    private WhatsAppService whatsAppService;

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    public List<Map<String, String>> fetchJobRelatedEmails() throws Exception {
        List<Map<String, String>> jobEmails = new ArrayList<>();

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(properties, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Folder jobRelatedFolder = store.getFolder("JOB RELATED");
        if(!jobRelatedFolder.exists()){
            jobRelatedFolder.create(Folder.HOLDS_MESSAGES);
        }

        //find unread messages
        Flags seen = new Flags(Flags.Flag.SEEN);  //getting all seen emails
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false); //using that to get unread emails

        //Filter to get only unread emails
        Message[] messages = inbox.search(unseenFlagTerm);

        //to only get unread emails under 7 days(or a week)
        Calendar cal = Calendar.getInstance(); // gives current date and time
        cal.add(Calendar.DAY_OF_MONTH, -7); // subtracts 7 days
        Date oneWeekAgo = cal.getTime(); //returns date object

        List<Message> remainingForGemini = new ArrayList<>();
        List<String> keywords = Arrays.asList(
                "job", "jobs", "career", "hiring", "intern", "internship",
                "placement", "opportunity", "vacancy", "openings", "recruitment",
                "walk-in", "trainee", "fresher", "interview", "job alert",
                "work from home", "remote job", "job opening", "position available",
                "apply now", "resume", "cv", "urgent requirement", "off-campus", "on-campus"
        );


        // Step 1: Keyword filter
        for (Message message : messages) {
            if (message.getReceivedDate() != null && message.getReceivedDate().after(oneWeekAgo)) {
                String subject = message.getSubject() != null ? message.getSubject() : "";
                String body = getTextFromMessage(message);

                boolean matches = keywords.stream().anyMatch(
                        keyword -> subject.toLowerCase().contains(keyword) || body.toLowerCase().contains(keyword)
                );

                if (matches) {
                    Map<String, String> emailData = new HashMap<>();
                    emailData.put("from", InternetAddress.toString(message.getFrom()));
                    emailData.put("to", InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
                    emailData.put("subject", subject);
                    emailData.put("body", body);
                    jobEmails.add(emailData);

                    String msg = "New Job Alert:\nFrom: " + emailData.get("from") +
                            "\nSubject: " + subject + "\nBody:\n" + body;
                    whatsAppService.sendWhatsAppMessage(msg);

                    inbox.copyMessages(new Message[]{message}, jobRelatedFolder);
                    message.setFlag(Flags.Flag.DELETED, true);
                } else {
                    remainingForGemini.add(message);
                }
            }
        }


        // Step 2: Gemini fallback only if no job emails found using keywords
        if (jobEmails.isEmpty()) {
            int maxGeminiChecks = 10;
            int geminiChecksDone = 0;
            for (Message message : remainingForGemini) {
                if (geminiChecksDone >= maxGeminiChecks) break;
                String subject = message.getSubject() != null ? message.getSubject() : "";
                String body = getTextFromMessage(message);

                if (geminiApiService.isJobRelated(subject, body)) {
                    Map<String, String> emailData = new HashMap<>();
                    emailData.put("from", InternetAddress.toString(message.getFrom()));
                    emailData.put("to", InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
                    emailData.put("subject", subject);
                    emailData.put("body", body);
                    jobEmails.add(emailData);

                    String msg = "New Job Alert (Gemini):\nFrom: " + emailData.get("from") +
                            "\nSubject: " + subject + "\nBody:\n" + body;
                    whatsAppService.sendWhatsAppMessage(msg);

//                    inbox.copyMessages(new Message[]{message}, jobRelatedFolder);
//                    message.setFlag(Flags.Flag.DELETED, true);
                    inbox.moveMessages(new Message[]{message}, jobRelatedFolder);

                }
                geminiChecksDone++;
            }
        }

        inbox.close(true);
        store.close();
        return jobEmails;
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        //checks if email is plain text email(no HTML, no attachments)
        if(message.isMimeType("text/plain")){
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if(bodyPart.isMimeType("text/plain")){
                result.append(bodyPart.getContent());
            }
        }
        return result.toString();
    }
}
