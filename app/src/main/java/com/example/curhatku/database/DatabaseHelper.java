// app/src/main/java/com/example/curhatku/database/DatabaseHelper.java

package com.example.curhatku.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.curhatku.models.Post;
import com.example.curhatku.models.Comment;
import com.example.curhatku.models.User;
import com.example.curhatku.models.Journal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "curhatku_db";
    private static final int DATABASE_VERSION = 1;

    // Tabel Posts
    private static final String TABLE_POSTS = "posts";
    private static final String COLUMN_POST_ID = "id";
    private static final String COLUMN_POST_CONTENT = "content";
    private static final String COLUMN_POST_CATEGORY = "category";
    private static final String COLUMN_POST_MOOD = "mood";
    private static final String COLUMN_POST_TIMESTAMP = "timestamp";
    private static final String COLUMN_POST_SUPPORT_COUNT = "supportCount";
    private static final String COLUMN_POST_IS_ANONYMOUS = "isAnonymous";

    // Tabel Comments
    private static final String TABLE_COMMENTS = "comments";
    private static final String COLUMN_COMMENT_ID = "id";
    private static final String COLUMN_COMMENT_POST_ID = "postId";
    private static final String COLUMN_COMMENT_CONTENT = "content";
    private static final String COLUMN_COMMENT_TIMESTAMP = "timestamp";
    private static final String COLUMN_COMMENT_IS_ANONYMOUS = "isAnonymous";
    private static final String COLUMN_COMMENT_AUTHOR_ID = "authorId";

    // Tabel Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD_HASH = "passwordHash";
    private static final String COLUMN_USER_AUTH_TOKEN = "authToken";
    private static final String COLUMN_USER_CREATED_AT = "createdAt";
    private static final String COLUMN_USER_LAST_LOGIN = "lastLogin";

    // Tabel Journals
    private static final String TABLE_JOURNALS = "journals";
    private static final String COLUMN_JOURNAL_ID = "id";
    private static final String COLUMN_JOURNAL_USER_ID = "userId";
    private static final String COLUMN_JOURNAL_DATE_TIMESTAMP = "dateTimestamp";
    private static final String COLUMN_JOURNAL_TITLE = "title";
    private static final String COLUMN_JOURNAL_CONTENT = "content";
    private static final String COLUMN_JOURNAL_TIMESTAMP = "timestamp";

    // Query CREATE TABLE
    private static final String CREATE_TABLE_POSTS = "CREATE TABLE " + TABLE_POSTS + "("
            + COLUMN_POST_ID + " TEXT PRIMARY KEY,"
            + COLUMN_POST_CONTENT + " TEXT,"
            + COLUMN_POST_CATEGORY + " TEXT,"
            + COLUMN_POST_MOOD + " TEXT,"
            + COLUMN_POST_TIMESTAMP + " INTEGER,"
            + COLUMN_POST_SUPPORT_COUNT + " INTEGER,"
            + COLUMN_POST_IS_ANONYMOUS + " INTEGER DEFAULT 1"
            + ")";

    private static final String CREATE_TABLE_COMMENTS = "CREATE TABLE " + TABLE_COMMENTS + "("
            + COLUMN_COMMENT_ID + " TEXT PRIMARY KEY,"
            + COLUMN_COMMENT_POST_ID + " TEXT,"
            + COLUMN_COMMENT_CONTENT + " TEXT,"
            + COLUMN_COMMENT_TIMESTAMP + " INTEGER,"
            + COLUMN_COMMENT_IS_ANONYMOUS + " INTEGER DEFAULT 1,"
            + COLUMN_COMMENT_AUTHOR_ID + " TEXT"
            + ")";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " TEXT PRIMARY KEY,"
            + COLUMN_USER_USERNAME + " TEXT UNIQUE,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_PASSWORD_HASH + " TEXT,"
            + COLUMN_USER_AUTH_TOKEN + " TEXT,"
            + COLUMN_USER_CREATED_AT + " INTEGER,"
            + COLUMN_USER_LAST_LOGIN + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_JOURNALS = "CREATE TABLE " + TABLE_JOURNALS + "("
            + COLUMN_JOURNAL_ID + " TEXT PRIMARY KEY,"
            + COLUMN_JOURNAL_USER_ID + " TEXT,"
            + COLUMN_JOURNAL_DATE_TIMESTAMP + " INTEGER,"
            + COLUMN_JOURNAL_TITLE + " TEXT,"
            + COLUMN_JOURNAL_CONTENT + " TEXT,"
            + COLUMN_JOURNAL_TIMESTAMP + " INTEGER"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_POSTS);
        db.execSQL(CREATE_TABLE_COMMENTS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_JOURNALS);

        insertInitialUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNALS);
        onCreate(db);
    }

    private void insertInitialUser(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, "user_123");
            values.put(COLUMN_USER_USERNAME, "user");
            values.put(COLUMN_USER_EMAIL, "user@example.com");
            values.put(COLUMN_USER_PASSWORD_HASH, "pass");
            values.put(COLUMN_USER_AUTH_TOKEN, (String) null);
            values.put(COLUMN_USER_CREATED_AT, System.currentTimeMillis());
            values.put(COLUMN_USER_LAST_LOGIN, System.currentTimeMillis());
            db.insert(TABLE_USERS, null, values);
            Log.d("DatabaseHelper", "Initial dummy user 'user' inserted.");
        }
    }


    // --- CRUD OPERATIONS FOR POSTS ---
    public long insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_ID, post.getId());
        values.put(COLUMN_POST_CONTENT, post.getContent());
        values.put(COLUMN_POST_CATEGORY, post.getCategory());
        values.put(COLUMN_POST_MOOD, post.getMood());
        values.put(COLUMN_POST_TIMESTAMP, post.getTimestamp());
        values.put(COLUMN_POST_SUPPORT_COUNT, post.getSupportCount());
        values.put(COLUMN_POST_IS_ANONYMOUS, post.isAnonymous() ? 1 : 0);

        long id = db.insertWithOnConflict(TABLE_POSTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_POSTS + " ORDER BY " + COLUMN_POST_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
                post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_CONTENT)));
                post.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_CATEGORY)));
                post.setMood(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_MOOD)));
                post.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_POST_TIMESTAMP)));
                post.setSupportCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_SUPPORT_COUNT)));
                post.setAnonymous(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_IS_ANONYMOUS)) == 1);
                postList.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return postList;
    }

    public Post getPostById(String postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, new String[]{
                        COLUMN_POST_ID, COLUMN_POST_CONTENT, COLUMN_POST_CATEGORY, COLUMN_POST_MOOD,
                        COLUMN_POST_TIMESTAMP, COLUMN_POST_SUPPORT_COUNT, COLUMN_POST_IS_ANONYMOUS},
                COLUMN_POST_ID + "=?", new String[]{String.valueOf(postId)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Post post = null;
        if (cursor != null && cursor.getCount() > 0) {
            post = new Post();
            post.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_ID)));
            post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_CONTENT)));
            post.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_CATEGORY)));
            post.setMood(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_MOOD)));
            post.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_POST_TIMESTAMP)));
            post.setSupportCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_SUPPORT_COUNT)));
            post.setAnonymous(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POST_IS_ANONYMOUS)) == 1);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return post;
    }

    public int updatePost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_CONTENT, post.getContent());
        values.put(COLUMN_POST_CATEGORY, post.getCategory());
        values.put(COLUMN_POST_MOOD, post.getMood());
        values.put(COLUMN_POST_TIMESTAMP, post.getTimestamp());
        values.put(COLUMN_POST_SUPPORT_COUNT, post.getSupportCount());
        values.put(COLUMN_POST_IS_ANONYMOUS, post.isAnonymous() ? 1 : 0);

        int rowsAffected = db.update(TABLE_POSTS, values, COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(post.getId())});
        db.close();
        return rowsAffected;
    }

    public void deletePost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, COLUMN_POST_ID + " = ?",
                new String[]{String.valueOf(post.getId())});
        db.close();
    }

    public int getPostCount() {
        String countQuery = "SELECT * FROM " + TABLE_POSTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // --- CRUD OPERATIONS FOR COMMENTS ---
    public long insertComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMENT_ID, comment.getId());
        values.put(COLUMN_COMMENT_POST_ID, comment.getPostId());
        values.put(COLUMN_COMMENT_CONTENT, comment.getContent());
        values.put(COLUMN_COMMENT_TIMESTAMP, comment.getTimestamp());
        values.put(COLUMN_COMMENT_IS_ANONYMOUS, comment.isAnonymous() ? 1 : 0);
        values.put(COLUMN_COMMENT_AUTHOR_ID, comment.getAuthorId());

        long id = db.insertWithOnConflict(TABLE_COMMENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public List<Comment> getCommentsByPostId(String postId) {
        List<Comment> commentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_COMMENTS + " WHERE " + COLUMN_COMMENT_POST_ID + " = ?" +
                " ORDER BY " + COLUMN_COMMENT_TIMESTAMP + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{postId});

        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                comment.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_ID)));
                comment.setPostId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_POST_ID)));
                comment.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_CONTENT)));
                comment.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_TIMESTAMP)));
                comment.setAnonymous(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_IS_ANONYMOUS)) == 1);
                comment.setAuthorId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT_AUTHOR_ID)));
                commentList.add(comment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return commentList;
    }

    public void saveComments(List<Comment> comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Comment comment : comments) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_COMMENT_ID, comment.getId());
                values.put(COLUMN_COMMENT_POST_ID, comment.getPostId());
                values.put(COLUMN_COMMENT_CONTENT, comment.getContent());
                values.put(COLUMN_COMMENT_TIMESTAMP, comment.getTimestamp());
                values.put(COLUMN_COMMENT_IS_ANONYMOUS, comment.isAnonymous() ? 1 : 0);
                values.put(COLUMN_COMMENT_AUTHOR_ID, comment.getAuthorId());
                db.insertWithOnConflict(TABLE_COMMENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    // --- CRUD OPERATIONS FOR USERS ---
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getId());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD_HASH, user.getPasswordHash());
        values.put(COLUMN_USER_AUTH_TOKEN, user.getAuthToken());
        values.put(COLUMN_USER_CREATED_AT, user.getCreatedAt());
        values.put(COLUMN_USER_LAST_LOGIN, user.getLastLogin());

        long id = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{
                        COLUMN_USER_ID, COLUMN_USER_USERNAME, COLUMN_USER_EMAIL,
                        COLUMN_USER_PASSWORD_HASH, COLUMN_USER_AUTH_TOKEN,
                        COLUMN_USER_CREATED_AT, COLUMN_USER_LAST_LOGIN},
                COLUMN_USER_USERNAME + "=?", new String[]{username},
                null, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD_HASH)));
            user.setAuthToken(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_AUTH_TOKEN)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_CREATED_AT)));
            user.setLastLogin(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_LAST_LOGIN)));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return user;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{
                        COLUMN_USER_ID, COLUMN_USER_USERNAME, COLUMN_USER_EMAIL,
                        COLUMN_USER_PASSWORD_HASH, COLUMN_USER_AUTH_TOKEN,
                        COLUMN_USER_CREATED_AT, COLUMN_USER_LAST_LOGIN},
                COLUMN_USER_ID + "=?", new String[]{userId},
                null, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD_HASH)));
            user.setAuthToken(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_AUTH_TOKEN)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_CREATED_AT)));
            user.setLastLogin(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_LAST_LOGIN)));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD_HASH, user.getPasswordHash());
        values.put(COLUMN_USER_AUTH_TOKEN, user.getAuthToken());
        values.put(COLUMN_USER_CREATED_AT, user.getCreatedAt());
        values.put(COLUMN_USER_LAST_LOGIN, user.getLastLogin());

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }


    // --- CRUD OPERATIONS FOR JOURNALS ---
    public long insertJournal(Journal journal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_JOURNAL_ID, journal.getId());
        values.put(COLUMN_JOURNAL_USER_ID, journal.getUserId());
        values.put(COLUMN_JOURNAL_DATE_TIMESTAMP, journal.getDateTimestamp());
        values.put(COLUMN_JOURNAL_TITLE, journal.getTitle());
        values.put(COLUMN_JOURNAL_CONTENT, journal.getContent());
        values.put(COLUMN_JOURNAL_TIMESTAMP, journal.getTimestamp());

        long id = db.insertWithOnConflict(TABLE_JOURNALS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public Journal getJournalById(String journalId) { // Mengambil jurnal berdasarkan ID (String)
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_JOURNALS, new String[]{
                        COLUMN_JOURNAL_ID, COLUMN_JOURNAL_USER_ID, COLUMN_JOURNAL_DATE_TIMESTAMP,
                        COLUMN_JOURNAL_TITLE, COLUMN_JOURNAL_CONTENT, COLUMN_JOURNAL_TIMESTAMP},
                COLUMN_JOURNAL_ID + "=?", new String[]{journalId}, // Menggunakan journalId langsung
                null, null, null, null);

        Journal journal = null;
        if (cursor != null && cursor.moveToFirst()) {
            journal = new Journal();
            journal.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_ID)));
            journal.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_USER_ID)));
            journal.setDateTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_DATE_TIMESTAMP)));
            journal.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TITLE)));
            journal.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_CONTENT)));
            journal.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TIMESTAMP)));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return journal;
    }

    public Journal getJournalByDateAndUser(String userId, Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        long dateMillis = date.getTime();

        Cursor cursor = db.query(TABLE_JOURNALS, new String[]{
                        COLUMN_JOURNAL_ID, COLUMN_JOURNAL_USER_ID, COLUMN_JOURNAL_DATE_TIMESTAMP,
                        COLUMN_JOURNAL_TITLE, COLUMN_JOURNAL_CONTENT, COLUMN_JOURNAL_TIMESTAMP},
                COLUMN_JOURNAL_USER_ID + "=? AND " + COLUMN_JOURNAL_DATE_TIMESTAMP + "=?",
                new String[]{userId, String.valueOf(dateMillis)},
                null, null, null, "1");

        Journal journal = null;
        if (cursor != null && cursor.moveToFirst()) {
            journal = new Journal();
            journal.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_ID)));
            journal.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_USER_ID)));
            journal.setDateTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_DATE_TIMESTAMP)));
            journal.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TITLE)));
            journal.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_CONTENT)));
            journal.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TIMESTAMP)));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return journal;
    }

    public List<Journal> getAllJournalsByUser(String userId) {
        List<Journal> journalList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_JOURNALS + " WHERE " + COLUMN_JOURNAL_USER_ID + " = ?" +
                " ORDER BY " + COLUMN_JOURNAL_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{userId});

        if (cursor.moveToFirst()) {
            do {
                Journal journal = new Journal();
                journal.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_ID)));
                journal.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_USER_ID)));
                journal.setDateTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_DATE_TIMESTAMP)));
                journal.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TITLE)));
                journal.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_CONTENT)));
                journal.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JOURNAL_TIMESTAMP)));
                journalList.add(journal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return journalList;
    }

    public void deleteJournal(String journalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_JOURNALS, COLUMN_JOURNAL_ID + " = ?",
                new String[]{journalId});
        db.close();
    }

    public void deleteJournalsByUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_JOURNALS, COLUMN_JOURNAL_USER_ID + " = ?",
                new String[]{userId});
        db.close();
    }

    public int getJournalCountByUser(String userId) {
        String countQuery = "SELECT * FROM " + TABLE_JOURNALS + " WHERE " + COLUMN_JOURNAL_USER_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{userId});
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_POSTS);
        db.execSQL("DELETE FROM " + TABLE_COMMENTS);
        db.execSQL("DELETE FROM " + TABLE_USERS);
        db.execSQL("DELETE FROM " + TABLE_JOURNALS);
        db.close();
    }
}