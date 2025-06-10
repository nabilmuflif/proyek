package com.example.curhatku.models;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String postId;
    private String content;
    private long timestamp;
    private boolean isAnonymous;
    private String authorId;
    private boolean supported;

    public Comment() {}

    public Comment(String id, String postId, String content, long timestamp, boolean isAnonymous) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.timestamp = timestamp;
        this.isAnonymous = isAnonymous;
    }

    public Comment(String id, String postId, String content, long timestamp, boolean isAnonymous, String authorId) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.timestamp = timestamp;
        this.isAnonymous = isAnonymous;
        this.authorId = authorId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public boolean isSupported() { return supported; }
    public void setSupported(boolean supported) { this.supported = supported; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Comment comment = (Comment) obj;
        return id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", postId='" + postId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isAnonymous=" + isAnonymous +
                ", authorId='" + authorId + '\'' +
                ", supported=" + supported +
                '}';
    }
}
