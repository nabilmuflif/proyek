package com.example.curhatku.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.curhatku.R;
import com.example.curhatku.adapters.CommentAdapter;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.Comment;
import com.example.curhatku.models.Post;
import com.example.curhatku.network.NetworkManager;
import com.example.curhatku.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostDetailActivity extends AppCompatActivity {

    private Post currentPost;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;

    private MaterialCardView postCard;
    private TextView tvPostContent;
    private TextView tvPostCategory;
    private TextView tvPostMood;
    private TextView tvPostTime;
    private TextView tvSupportCount;
    private MaterialButton btnSupport;
    private MaterialButton btnThemeToggle;
    private ImageButton btnBack;

    private RecyclerView recyclerComments;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etComment;
    private MaterialButton btnSendComment;

    private Handler mainHandler;
    private ExecutorService executorService;
    private NetworkManager networkManager;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);

        setContentView(R.layout.activity_post_detail);

        String postId = getIntent().getStringExtra("POST_ID");
        if (postId == null) {
            finish();
            return;
        }

        networkManager = NetworkManager.getInstance(this);
        databaseManager = DatabaseManager.getInstance(getApplicationContext());

        initViews();
        setupRecyclerView();
        setupClickListeners();

        loadPostDetail(postId);
        loadComments(postId);
    }

    private void initViews() {
        postCard = findViewById(R.id.post_card);
        tvPostContent = findViewById(R.id.tv_post_content);
        tvPostCategory = findViewById(R.id.tv_post_category);
        tvPostMood = findViewById(R.id.tv_post_mood);
        tvPostTime = findViewById(R.id.tv_post_time);
        tvSupportCount = findViewById(R.id.tv_support_count);
        btnSupport = findViewById(R.id.btn_support);
        btnThemeToggle = findViewById(R.id.btn_theme_toggle);
        btnBack = findViewById(R.id.btn_back);

        recyclerComments = findViewById(R.id.recycler_comments);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        etComment = findViewById(R.id.et_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);

        mainHandler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();

        updateThemeButton();
    }

    private void setupRecyclerView() {
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(commentAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnThemeToggle.setOnClickListener(v -> {
            ThemeManager.toggleTheme(this);
            recreate();
        });
        btnSupport.setOnClickListener(v -> supportPost());

        btnSendComment.setOnClickListener(v -> sendComment());

        swipeRefresh.setOnRefreshListener(() -> {
            if (currentPost != null) {
                loadComments(currentPost.getId());
            }
        });
    }

    private void loadPostDetail(String postId) {
        executorService.execute(() -> {
            Post localPost = databaseManager.getPostById(postId);

            if (localPost != null) {
                currentPost = localPost;
                mainHandler.post(this::displayPost);
            } else {
                mainHandler.post(() -> {
                    Toast.makeText(this, "Post tidak ditemukan secara lokal", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void loadComments(String postId) {
        executorService.execute(() -> {
            List<Comment> localComments = databaseManager.getCommentsByPostId(postId);
            mainHandler.post(() -> {
                commentList.clear();
                commentList.addAll(localComments);
                commentAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            });
        });
    }


    private void displayPost() {
        if (currentPost == null) return;
        tvPostContent.setText(currentPost.getContent());
        tvPostCategory.setText(getCategoryEmoji(currentPost.getCategory()) + " " + currentPost.getCategory());
        tvPostMood.setText(getMoodEmoji(currentPost.getMood()) + " " + currentPost.getMood());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        tvPostTime.setText(sdf.format(new Date(currentPost.getTimestamp())));

        tvSupportCount.setText(String.valueOf(currentPost.getSupportCount()));
        btnSupport.setText("💜 Beri Dukungan (" + currentPost.getSupportCount() + ")");
    }

    private void supportPost() {
        if (currentPost == null) return;

        executorService.execute(() -> {
            currentPost.setSupportCount(currentPost.getSupportCount() + 1);
            databaseManager.updatePost(currentPost);

            mainHandler.post(() -> {
                displayPost();
                Toast.makeText(getApplicationContext(), "Dukungan tersimpan (offline) 💜", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void sendComment() {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Tulis komentar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPost == null) return;

        Comment newComment = new Comment(
                UUID.randomUUID().toString(),
                currentPost.getId(),
                commentText,
                System.currentTimeMillis(),
                true // anonymous
        );

        executorService.execute(() -> {
            databaseManager.saveComment(newComment);
            mainHandler.post(() -> {
                commentList.add(0, newComment);
                commentAdapter.notifyItemInserted(0);
                recyclerComments.scrollToPosition(0);
                etComment.setText("");
                Toast.makeText(getApplicationContext(), "Komentar tersimpan (offline) 💬", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateThemeButton() {
        if (ThemeManager.isDarkTheme(this)) {
            btnThemeToggle.setText("☀️");
        } else {
            btnThemeToggle.setText("🌙");
        }
    }

    private String getCategoryEmoji(String category) {
        switch (category.toLowerCase()) {
            case "keluarga": return "👨‍👩‍👧‍👦";
            case "cinta": return "💕";
            case "pekerjaan": return "💼";
            case "kesehatan mental": return "🧠";
            case "pendidikan": return "📚";
            case "keuangan": return "💰";
            default: return "💭";
        }
    }

    private String getMoodEmoji(String mood) {
        switch (mood.toLowerCase()) {
            case "senang": return "😊";
            case "sedih": return "😢";
            case "marah": return "😠";
            case "bingung": return "😕";
            case "takut": return "😰";
            case "kecewa": return "😞";
            case "lelah": return "😴";
            default: return "😐";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}