// app/src/main/java/com/example/curhatku/models/Quote.java

package com.example.curhatku.models;

import com.google.gson.annotations.SerializedName; // Import ini

public class Quote {
    // Hapus _id jika tidak ada di API baru
    // private String _id;

    @SerializedName("q") // Sesuaikan dengan nama field di JSON respons ZenQuotes.io
    private String content;

    @SerializedName("a") // Sesuaikan dengan nama field di JSON respons ZenQuotes.io
    private String author;

    // Hapus properti yang tidak ada di API baru jika tidak digunakan
    // private String[] tags;
    // private int length;

    public Quote() {}

    public Quote(String content, String author) {
        this.content = content;
        this.author = author;
    }

    // Sesuaikan getter/setter untuk id jika Anda menghapusnya, dan untuk properti baru
    // public String getId() { return _id; }
    // public void setId(String id) { this._id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    // Hapus getter/setter untuk properti yang dihapus
    // public String[] getTags() { return tags; }
    // public void setTags(String[] tags) { this.tags = tags; }
    // public int getLength() { return length; }
    // public void setLength(int length) { this.length = length; }

    @Override
    public String toString() {
        return "Quote{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}'; // Sesuaikan toString
    }
}