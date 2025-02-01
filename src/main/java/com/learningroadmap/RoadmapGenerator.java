package com.learningroadmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class RoadmapGenerator {
    private final Map<String, String> difficultyLevels;
    private final Map<String, Integer> timeEstimates;
    private final String openaiApiKey;
    private final String openaiApiBase;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public RoadmapGenerator(String openaiApiKey, String openaiApiBase) {
        this.openaiApiKey = openaiApiKey;
        this.openaiApiBase = openaiApiBase;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();

        this.difficultyLevels = new HashMap<>();
        difficultyLevels.put("مبتدی", "beginner");
        difficultyLevels.put("متوسط", "intermediate");
        difficultyLevels.put("پیشرفته", "advanced");

        this.timeEstimates = new HashMap<>();
        timeEstimates.put("هفته", 7);
        timeEstimates.put("ماه", 30);
        timeEstimates.put("سال", 365);
    }

    private int parseTimeCommitment(String timeStr) {
        for (Map.Entry<String, Integer> entry : timeEstimates.entrySet()) {
            if (timeStr.contains(entry.getKey())) {
                try {
                    int number = Integer.parseInt(timeStr.replaceAll("\\D+", ""));
                    return number * entry.getValue();
                } catch (NumberFormatException e) {
                    return 30;
                }
            }
        }
        return 30;
    }

    private String getResourcePrompt(String subject, String level, int timeAvailableDays,
                                     String resourceType) {
        return String.format("""
            به عنوان یک متخصص برنامه‌نویسی و هوش مصنوعی، یک نقشه‌راه یادگیری شخصی‌سازی شده ایجاد کنید:

            موضوع: %s
            سطح: %s
            زمان در دسترس: %d روز
            نوع منابع ترجیحی: %s

                لطفاً یک برنامه یادگیری دقیق با موارد زیر ارائه دهید:
                
                  1. اهداف یادگیری هفتگی
                  2. منابع پیشنهادی
                  3. پروژه‌های عملی
                  4. نقاط عطف کلیدی
                  5. روش‌های ارزیابی
                  
            """, subject, level, timeAvailableDays, resourceType);
    }

    public String generateRoadmap(String subject, String level, String timeAvailable,

                                  String resourceType, List<Course> courses) {
        try {
            int days = parseTimeCommitment(timeAvailable);
            String prompt = getResourcePrompt(subject, level, days, resourceType);
            System.out.println(courses);

            if (!courses.isEmpty()) {
                StringBuilder coursesPrompt = new StringBuilder("\n\nدوره‌های مرتبط یافت شده:\n");
                for (Course course : courses) {
                    coursesPrompt.append(String.format("- %s: %s\n",
                            course.getName(), course.getShortDescription()));
                }
                prompt += coursesPrompt.toString();

            }
            System.out.println(prompt);

            // Ensure the API base URL is properly formatted
            String apiUrl = openaiApiBase;
            if (!apiUrl.startsWith("http")) {
                apiUrl = "https://" + apiUrl;
            }
            if (!apiUrl.endsWith("/")) {
                apiUrl += "/";
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", Arrays.asList(
                    Map.of("role", "system", "content", "شما یک متخصص آموزش برنامه‌نویسی و هوش مصنوعی هستید."),
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("timeout",50);

            RequestBody body = RequestBody.create(
                    mapper.writeValueAsString(requestBody),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(apiUrl + "chat/completions")
                    .addHeader("Authorization", "Bearer " + openaiApiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("API Error Response: " + response.body().string());
                    throw new IOException("Unexpected response " + response);
                }

                Map<String, Object> responseData = mapper.readValue(response.body().string(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseData.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Add this for better error tracking
            System.err.println("Error generating roadmap: " + e.getMessage());
            return "Error generating roadmap. Please try again.";
        }
    }
}
