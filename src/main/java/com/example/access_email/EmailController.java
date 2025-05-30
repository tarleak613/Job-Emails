package com.example.access_email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emails")
public class EmailController {

        @Autowired
        private EmailReaderService emailReaderService;

        @Autowired
        private EmailWriterService emailWriterService;

        @GetMapping
        public List<Map<String, String>> getEmails() throws Exception {
            return emailReaderService.fetchEmails();
        }

        @GetMapping("/jobEmails")
        public List<Map<String, String>> getJobEmails() {
                try {
                        return emailWriterService.fetchJobRelatedEmails();
                } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch job-related emails", e);
                }
        }
}
