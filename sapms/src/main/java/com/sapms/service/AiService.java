package com.sapms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    private static final String URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openrouter/auto";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String chat(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI service is not configured. Please add your OpenRouter API key to application.properties (openrouter.api.key=YOUR_KEY).";
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", MODEL,
                    "max_tokens", 1024,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)
                    )
            );
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "http://localhost:8080")
                    .header("X-Title", "SAPMS Academic System")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(resp.body());

            if (root.has("error")) {
                return "AI error: " + root.path("error").path("message").asText("Unknown error");
            }

            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return "AI returned no response. Status: " + resp.statusCode();
            }

            // DeepSeek R1 sometimes wraps answer in <think> tags — strip them
            String content = choices.get(0).path("message").path("content").asText("No response.");
            content = content.replaceAll("(?s)<think>.*?</think>", "").trim();
            return content;

        } catch (Exception e) {
            return "AI service error: " + e.getMessage();
        }
    }

    public String analyzeStudentPerformance(String studentName, String gradesData, String attendanceData) {
        String system = "You are an academic performance advisor for a university. Analyze student data and provide constructive, encouraging feedback. Be specific, actionable, and supportive. Keep response under 300 words. Use clear sections.";
        String user = String.format(
                "Student: %s\n\nGrade Data:\n%s\n\nAttendance Data:\n%s\n\nPlease provide: 1) Performance summary 2) Strengths 3) Areas needing improvement 4) Specific study recommendations",
                studentName, gradesData, attendanceData);
        return chat(system, user);
    }

    public String generateEarlyWarning(String courseName, String studentsData) {
        String system = "You are an academic analytics assistant for university lecturers. Analyze student performance data and identify students at risk of failing. Be concise and actionable. Keep response under 400 words.";
        String user = String.format(
                "Course: %s\n\nStudent Performance Data:\n%s\n\nPlease provide: 1) At-risk students with reasons 2) Students showing improvement 3) Recommended interventions 4) Overall class summary",
                courseName, studentsData);
        return chat(system, user);
    }

    public String summarizeReport(String reportData) {
        String system = "You are an academic data analyst. Summarize academic performance reports clearly and professionally. Highlight key insights, trends, and recommendations. Keep response under 400 words.";
        String user = "Analyze this academic performance data and provide an executive summary:\n\n" + reportData;
        return chat(system, user);
    }
}
