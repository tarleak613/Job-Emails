package com.example.access_email;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class WhatsAppService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.from_whatsapp}")
    private String fromWhatsApp;

    @Value("${twilio.to_whatsapp}")
    private String toWhatsApp;

    @PostConstruct
    public void init(){
        System.out.println("SID: " + accountSid); // Debug
        System.out.println("Token: " + authToken); // Debug
        Twilio.init(accountSid, authToken);
    }

    public void sendWhatsAppMessage(String messageBody){
        final int MAX_BYTES = 1590;
        byte[] messageBytes = messageBody.getBytes(StandardCharsets.UTF_8);
        if (messageBytes.length > MAX_BYTES) {
            // Truncate safely without splitting multi-byte characters
            int end = messageBody.length();
            while (new String(messageBody.substring(0, end).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8).getBytes().length > MAX_BYTES - 15) {
                end--;
            }
            messageBody = messageBody.substring(0, end) + "\n[truncated]";
        }
        Message message = Message.creator(
                new PhoneNumber(toWhatsApp),
                new PhoneNumber(fromWhatsApp),
                messageBody
        ).create();

        System.out.println("Message sent with SID: "+ message.getSid());
    }
}
