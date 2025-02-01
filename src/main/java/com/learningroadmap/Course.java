package com.learningroadmap;

public class Course {
    private String name;
    private String shortDescription;
    private String level;
    private String homepageUrl;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getHomepageUrl() { return homepageUrl; }
    public void setHomepageUrl(String homepageUrl) { this.homepageUrl = homepageUrl; }
}