package com.example.curhatku.models;

import java.io.Serializable;

public class Post implements Serializable {
    private String id;
    private String content;
    private String category;
    private String mood;
    private long timestamp;
    private int supportCount;
    private boolean isAnonymous;

    public Post() {}

    public Post(String id, String content, String category, String mood, long timestamp) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.mood = mood;
        this.timestamp = timestamp;
        this.supportCount = 0;
        this.isAnonymous = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getSupportCount() { return supportCount; }
    public void setSupportCount(int supportCount) { this.supportCount = supportCount; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }
}
