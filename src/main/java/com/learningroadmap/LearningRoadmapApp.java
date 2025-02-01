package com.learningroadmap;

import java.util.List;
import java.util.Scanner;

public class LearningRoadmapApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Load configuration
        String openaiApiKey = "aa-yG9Hef5CRZuLA4nbnNnMmjqi7xkWvd1FbQnFUKRHfJXlK75I";
        String courseraApiKey = "sN9ytIjYvEIlt0mEH0s0SH28tgDfRW4gGxK0tvLiHWIzUfed";
        String openaiApiBase = "https://api.avalai.ir/v1";

// Initialize services
        CourseProvider courseProvider = new CourseProvider(courseraApiKey);
        RoadmapGenerator roadmapGenerator = new RoadmapGenerator(openaiApiKey, openaiApiBase);

        // Get user inputs
        System.out.println("Welcome to Learning Roadmap Generator!");
        System.out.println("=====================================");

        System.out.print("What subject would you like to learn? (e.g., Python Programming): ");
        String subject = scanner.nextLine();

        System.out.println("\nChoose your level:");
        System.out.println("1. مبتدی (Beginner)");
        System.out.println("2. متوسط (Intermediate)");
        System.out.println("3. پیشرفته (Advanced)");
        System.out.print("Enter your choice (1-3): ");
        int levelChoice = Integer.parseInt(scanner.nextLine());
        String level = switch (levelChoice) {
            case 1 -> "مبتدی";
            case 2 -> "متوسط";
            case 3 -> "پیشرفته";
            default -> "مبتدی";
        };

        System.out.print("\nHow much time do you have available? (e.g., 3 ماه or 2 هفته): ");
        String timeAvailable = scanner.nextLine();

        System.out.println("\nChoose your preferred resource types (comma-separated):");
        System.out.println("1. دوره‌های آنلاین (Online Courses)");
        System.out.println("2. کتاب‌ها (Books)");
        System.out.println("3. ویدیوها (Videos)");
        System.out.println("4. پروژه‌های عملی (Practical Projects)");
        System.out.print("Enter your choices (e.g., 1,4): ");
        String resourceChoices = scanner.nextLine();
        String resourceType = convertResourceChoices(resourceChoices);

        System.out.println();
        System.out.println("\nGenerating your personalized learning roadmap...");

        // Search for relevant courses
        List<Course> courses = courseProvider.searchCourses(
                subject,
                convertLevelToEnglish(level),
                subject.toLowerCase().contains("python") ? "python" : "ai",
                5
        );

        // Generate roadmap
        String roadmap = roadmapGenerator.generateRoadmap(
                subject,
                level,
                timeAvailable,
                resourceType,
                courses
        );

        // Print results
        System.out.println("\nGenerated Roadmap:");
        System.out.println("=================");
        System.out.println(roadmap);

        System.out.println("\nRelevant Courses:");
        System.out.println("================");
        for (Course course : courses) {
            System.out.println("Course: " + course.getName());
            System.out.println("Description: " + course.getShortDescription());
            System.out.println("Level: " + course.getLevel());
            System.out.println("URL: " + course.getHomepageUrl());
            System.out.println();
        }

        scanner.close();
    }

    private static String convertResourceChoices(String choices) {
        StringBuilder result = new StringBuilder();
        String[] nums = choices.split(",");
        for (String num : nums) {
            switch (num.trim()) {
                case "1" -> result.append("دوره‌های آنلاین, ");
                case "2" -> result.append("کتاب‌ها, ");
                case "3" -> result.append("ویدیوها, ");
                case "4" -> result.append("پروژه‌های عملی, ");
            }
        }
        return result.length() > 0 ? result.substring(0, result.length() - 2) : "دوره‌های آنلاین";
    }

    private static String convertLevelToEnglish(String persianLevel) {
        return switch (persianLevel) {
            case "مبتدی" -> "beginner";
            case "متوسط" -> "intermediate";
            case "پیشرفته" -> "advanced";
            default -> "beginner";
        };
    }
}