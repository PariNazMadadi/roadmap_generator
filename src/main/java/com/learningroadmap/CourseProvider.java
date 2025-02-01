package com.learningroadmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class CourseProvider {
    private final String apiKey;
    private final String apiUrl = "https://api.coursera.org/api/courses.v1";
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public CourseProvider(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }
    public List<Course> searchCourses(String query, String level, String category, int pageSize) {
        try {
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            String encodedLevel = java.net.URLEncoder.encode(level, "UTF-8");

            String url = String.format("%s?q=search&query=%s&level=%s&limit=%d",
                    apiUrl, encodedQuery, encodedLevel, pageSize);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Coursera API Error: " + response.body().string());
                    throw new IOException("Unexpected response " + response);
                }

                String responseBody = response.body().string();
                System.out.println("Coursera API Response: " + responseBody); // Debug line

                Map<String, Object> responseData = mapper.readValue(responseBody, Map.class);
                List<Map<String, Object>> elements = (List<Map<String, Object>>) responseData.get("elements");

                if (elements == null) {
                    System.err.println("No elements in response");
                    return new ArrayList<>();
                }
                System.out.println(elements);


                List<Course> courses = new ArrayList<>();
                for (Map<String, Object> element : elements) {
                    if (element.get("name").toString().toLowerCase().contains(category.toLowerCase())) {
                        Course course = new Course();
                        course.setName(getString(element, "name"));
                        course.setShortDescription(getString(element, "description")); // Changed from shortDescription
                        course.setLevel(getString(element, "level"));
                        course.setHomepageUrl(getString(element, "link")); // Changed from homepageUrl
                        courses.add(course);
                    }
                }
                System.out.println("Found " + courses.size() );
                System.out.println();
                return courses;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Add this for better error tracking
            System.err.println("Error fetching courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }}