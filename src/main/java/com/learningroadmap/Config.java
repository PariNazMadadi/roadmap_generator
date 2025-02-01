package com.learningroadmap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static Map<String, String> envVars = new HashMap<>();

    static {
        try {
            // Try multiple possible locations for .env file
            String[] possibleNames = {".env", ".env.", "env.txt"};
            boolean loaded = false;

            for (String name : possibleNames) {
                Path path = Paths.get(name);
                if (path.toFile().exists()) {
                    loadEnvFile(path.toString());
                    loaded = true;
                    break;
                }
            }

            if (!loaded) {
                System.err.println("Warning: No environment file found. Using system environment variables.");
            }
        } catch (Exception e) {
            System.err.println("Error loading environment: " + e.getMessage());
        }
    }

    private static void loadEnvFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        envVars.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading " + filename + ": " + e.getMessage());
        }
    }

    public static String getEnvVar(String key) {
        // First check our loaded env vars, then system environment
        String value = envVars.get(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value;
    }
}