package com.example.curhatku.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.curhatku.models.Post;
import com.example.curhatku.models.Comment;
import com.example.curhatku.models.Quote;
import com.example.curhatku.database.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.net.ssl.SSLHandshakeException;

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance;

    private final QuoteApiService quoteApiService;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context;
    private DatabaseManager databaseManager;

    private NetworkManager(Context context) {
        this.context = context.getApplicationContext();
        this.quoteApiService = RetrofitClient.getQuoteApiService();
        this.executorService = Executors.newFixedThreadPool(3);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.databaseManager = DatabaseManager.getInstance(context);
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public interface NetworkCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
        void onFailure(Throwable throwable);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // ========== POST METHODS (DARI DATABASE LOKAL) ==========
    public void getPosts(NetworkCallback<List<Post>> callback) {
        executeInBackground(() -> {
            List<Post> posts = databaseManager.getAllPosts();
            mainHandler.post(() -> callback.onSuccess(posts));
        });
    }

    public void createPost(Post post, NetworkCallback<Post> callback) {
        executeInBackground(() -> {
            databaseManager.savePost(post);
            mainHandler.post(() -> callback.onSuccess(post));
        });
    }

    public void getPost(String postId, NetworkCallback<Post> callback) {
        executeInBackground(() -> {
            Post post = databaseManager.getPostById(postId);
            final Post finalFoundPost = post;
            if (finalFoundPost != null) {
                mainHandler.post(() -> callback.onSuccess(finalFoundPost));
            } else {
                mainHandler.post(() -> callback.onError("Post tidak ditemukan di database lokal."));
            }
        });
    }

    public void searchPosts(String query, int page, int limit, NetworkCallback<List<Post>> callback) {
        executeInBackground(() -> {
            List<Post> filteredPosts = databaseManager.searchPosts(query);
            mainHandler.post(() -> callback.onSuccess(filteredPosts));
        });
    }

    public void getCategories(NetworkCallback<List<String>> callback) {
        executeInBackground(() -> {
            List<String> categories = new ArrayList<>();
            categories.add("Semua");
            categories.add("Pekerjaan");
            categories.add("Keluarga");
            categories.add("Cinta");
            categories.add("Kesehatan Mental");
            categories.add("Pencapaian");
            categories.add("Persahabatan");
            categories.add("Pendidikan");
            categories.add("Keuangan");
            categories.add("Hobi");
            mainHandler.post(() -> callback.onSuccess(categories));
        });
    }

    // ========== COMMENT METHODS (DARI DATABASE LOKAL) ==========
    public void getComments(String postId, NetworkCallback<List<Comment>> callback) {
        executeInBackground(() -> {
            List<Comment> comments = databaseManager.getCommentsByPostId(postId);
            mainHandler.post(() -> callback.onSuccess(comments));
        });
    }

    public void addComment(String postId, Comment comment, NetworkCallback<Comment> callback) {
        executeInBackground(() -> {
            databaseManager.saveComment(comment);
            mainHandler.post(() -> callback.onSuccess(comment));
        });
    }

    // ========== SUPPORT METHODS (LOKAL) ==========
    public void supportPost(String postId, NetworkCallback<Void> callback) {
        executeInBackground(() -> {
            mainHandler.post(() -> callback.onSuccess(null));
        });
    }

    // ========== QUOTE METHODS (HANYA API EKSTERNAL, TANPA FALLBACK MOCK) ==========
    @SuppressWarnings("rawtypes") // Suppress warning for raw type, because Response<List<Quote>> is specific
    public void getRandomQuote(NetworkCallback<Quote> callback) {
        if (!isNetworkAvailable()) {
            Log.w(TAG, "Tidak ada koneksi internet saat mencoba mengambil kutipan.");
            callback.onError("Tidak ada jaringan, coba hubungkan internet.");
            callback.onFailure(new Exception("No network available for quote API."));
            return;
        }

        Call<List<Quote>> call = quoteApiService.getRandomQuote(); // <-- UBAH KE LIST<QUOTE>
        call.enqueue(new Callback<List<Quote>>() { // <-- UBAH KE LIST<QUOTE>
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) { // <-- UBAH KE LIST<QUOTE>
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // ZenQuotes.io mengembalikan array, jadi ambil elemen pertama
                    callback.onSuccess(response.body().get(0)); // <-- AMBIL ELEMEN PERTAMA
                } else {
                    String errorMessage = "Gagal mengambil kutipan dari API.";
                    if (response.message() != null && !response.message().isEmpty()) {
                        errorMessage += " Pesan: " + response.message();
                    }
                    Log.e(TAG, "API Error: " + errorMessage);
                    callback.onError(errorMessage);
                    callback.onFailure(new Exception("API response not successful: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) { // <-- UBAH KE LIST<QUOTE>
                Log.e(TAG, "Network error fetching quote: " + t.getMessage(), t);

                String errorMessage;
                if (t instanceof SSLHandshakeException) {
                    errorMessage = "Masalah keamanan koneksi (sertifikat API kedaluwarsa).";
                } else {
                    errorMessage = "Terjadi kesalahan jaringan saat mengambil kutipan.";
                }

                callback.onError(errorMessage);
                callback.onFailure(t);
            }
        });
    }

    // ========== UTILITY METHODS ==========
    public <T> void executeInBackground(Runnable task) {
        executorService.execute(task);
    }

    public void postToMainThread(Runnable task) {
        mainHandler.post(task);
    }

    public void cancelAllRequests() {
        Log.d(TAG, "Cancelling all network requests");
    }

    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // ========== MOCK DATA METHODS (HANYA UNTUK DATA AWAL LOKAL JIKA DIBUTUHKAN) ==========
    // Metode ini hanya akan digunakan jika Anda secara eksplisit memanggilnya (misal, untuk sample data jika DB kosong).
    // Tidak lagi dipanggil sebagai fallback untuk API kutipan.
    public void getMockRandomQuote(NetworkCallback<Quote> callback) {
        executeInBackground(() -> {
            try {
                Thread.sleep(300);
                String[] quotes = {
                        "Setiap hari adalah kesempatan baru untuk menjadi versi terbaik dari dirimu."
                };
                String[] authors = {
                        "CurhatKu"
                };
                // Mengurangi variasi mock data karena ini tidak akan selalu ditampilkan.
                // Jika Anda ingin lebih banyak variasi, Anda bisa tambahkan di sini.
                Quote mockQuote = new Quote(quotes[0], authors[0]);
                mainHandler.post(() -> callback.onSuccess(mockQuote));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                mainHandler.post(() -> callback.onFailure(e));
            }
        });
    }
}