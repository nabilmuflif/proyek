package com.example.curhatku.database;

import android.content.Context;
import com.example.curhatku.models.Comment;
import com.example.curhatku.models.Post;
import com.example.curhatku.models.User;
import com.example.curhatku.models.Journal;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseHelper dbHelper;
    private ExecutorService executor;

    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        executor = Executors.newFixedThreadPool(4);
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    // Post operations
    public void savePosts(List<Post> posts) {
        executor.execute(() -> {
            for (Post post : posts) {
                dbHelper.insertPost(post);
            }
        });
    }

    public void savePost(Post post) {
        executor.execute(() -> {
            dbHelper.insertPost(post);
        });
    }

    public List<Post> getAllPosts() {
        return dbHelper.getAllPosts();
    }

    public Post getPostById(String postId) {
        return dbHelper.getPostById(postId);
    }

    public void updatePost(Post post) {
        executor.execute(() -> {
            dbHelper.updatePost(post);
        });
    }

    public void deletePost(Post post) {
        executor.execute(() -> {
            dbHelper.deletePost(post);
        });
    }

    public int getPostCount() {
        return dbHelper.getPostCount();
    }

    public List<Post> searchPosts(String query) {
        // Metode ini memerlukan implementasi di DatabaseHelper jika ingin search di DB
        return new ArrayList<>(); // Placeholder jika tidak diimplementasikan
    }


    // Comment operations
    public void saveComments(List<Comment> comments) {
        executor.execute(() -> {
            dbHelper.saveComments(comments);
        });
    }

    public void saveComment(Comment comment) {
        executor.execute(() -> {
            dbHelper.insertComment(comment);
        });
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return dbHelper.getCommentsByPostId(postId);
    }



    // User operations
    public void saveUser(User user) {
        executor.execute(() -> {
            dbHelper.insertUser(user);
        });
    }


    public User getUserByUsername(String username) {
        return dbHelper.getUserByUsername(username);
    }

    public User getUserById(String userId) {
        return dbHelper.getUserById(userId);
    }

    public void updateUser(User user) {
        executor.execute(() -> {
            dbHelper.updateUser(user);
        });
    }

    public void deleteUser(User user) {
        executor.execute(() -> {
            dbHelper.deleteUser(user);
        });
    }


    // Journal operations
    public void saveJournal(Journal journal) {
        executor.execute(() -> {
            dbHelper.insertJournal(journal);
        });
    }

    public List<Journal> getAllJournalsByUser(String userId) {
        return dbHelper.getAllJournalsByUser(userId);
    }

    public void deleteJournal(String journalId) {
        executor.execute(() -> {
            dbHelper.deleteJournal(journalId);
        });
    }

    public Journal getJournalById(String journalId) {
        // Catatan: Ini adalah operasi READ, jadi tidak perlu di dalam executor.execute()
        // dbHelper.getJournalById() sudah menangani Thread di dalamnya
        return dbHelper.getJournalById(journalId);
    }

    public int getJournalCountByUser(String userId) {
        return dbHelper.getJournalCountByUser(userId);
    }


    public void clearAllData() {
        executor.execute(() -> {
            dbHelper.clearAllTables();
        });
    }
}