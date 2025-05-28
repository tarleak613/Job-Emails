package com.example.access_email;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeminiApiService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

//    boolean isJobRelated(String subject, String body) {
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//            String prompt = "Is this email related to a job opportunity? Reply with only 'yes' or 'no'.\n\n"
//                    + "Subject: " + subject + "\n\nBody: " + body;
//
//            String requestBody = "{\n" +
//                    "  \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt.replace("\"", "\\\"") + "\" }] }]\n" +
//                    "}";
//
//            String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(fullUrl))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            String responseBody = response.body().toLowerCase();
//
//            System.out.println("Gemini full response: " + responseBody);
//            return responseBody.contains("yes");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
public boolean isJobRelated(String subject, String body) {
    try {
        String prompt = String.format("""
                    Is this email related to a job opportunity for freshers in India?
                    Reply only with "yes" or "no".

                    Subject: %s
                    Body: %s
                    """, subject, body);

        String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": "%s"
                            }
                          ]
                        }
                      ]
                    }
                    """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body().toLowerCase();
        System.out.println("Gemini Response: " + responseBody);

        // Basic check
        return responseBody.contains("yes");

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}

