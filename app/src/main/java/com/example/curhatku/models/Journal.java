package com.example.curhatku.models;

import java.io.Serializable;
import java.util.Date;

public class Journal implements Serializable {

    private String id;
    private String userId;
    private long dateTimestamp;
    private String title;
    private String content;
    private long timestamp;

    public Journal() {}

    public Journal(String id, String userId, long dateTimestamp, String title, String content, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.dateTimestamp = dateTimestamp;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getDateTimestamp() { return dateTimestamp; }
    public void setDateTimestamp(long dateTimestamp) { this.dateTimestamp = dateTimestamp; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Journal{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", dateTimestamp=" + dateTimestamp +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}