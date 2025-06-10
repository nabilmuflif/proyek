package com.example.curhatku.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.curhatku.R;
import com.example.curhatku.activities.PostDetailActivity;
import com.example.curhatku.adapters.PostAdapter;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.Post;
import com.example.curhatku.network.NetworkManager; // Tetap import NetworkManager untuk Quote API jika digunakan di sini
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabCreate;

    private ExecutorService executorService;
    private Handler mainHandler;
    private NetworkManager networkManager; // Tetap ada jika Anda menggunakannya untuk Quote API di fragment lain
    private DatabaseManager databaseManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        networkManager = NetworkManager.getInstance(requireContext()); // Tetap inisialisasi
        databaseManager = DatabaseManager.getInstance(requireContext());

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadPosts();

        fabCreate.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Create new post clicked!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigate(R.id.createPostFragment);
        });

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_posts);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        fabCreate = view.findViewById(R.id.fab_create);
    }

    private void setupRecyclerView() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(postAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadPosts);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
    }

    private void loadPosts() {
        swipeRefresh.setRefreshing(true);
        executorService.execute(() -> {
            if (!isAdded()) return;


            List<Post> localPosts = databaseManager.getAllPosts();
            mainHandler.post(() -> {
                postList.clear();
                postList.addAll(localPosts);
                postAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);

                if (localPosts.isEmpty()) {
                    addSamplePosts();
                }
            });

        });
    }

    private void addSamplePosts() {
        List<Post> samplePosts = new ArrayList<>();
        samplePosts.add(new Post(UUID.randomUUID().toString(),
                "Hari ini aku merasa sangat senang karena akhirnya lulus dari universitas. Terima kasih untuk semua dukungannya selama ini!",
                "Pendidikan", "Happy", System.currentTimeMillis()));
        samplePosts.add(new Post(UUID.randomUUID().toString(),
                "Sedang bingung harus mengambil keputusan yang berat dalam hidup. Ada yang pernah mengalami hal serupa?",
                "Lainnya", "Confused", System.currentTimeMillis() - 3600000));
        samplePosts.add(new Post(UUID.randomUUID().toString(),
                "Hubungan dengan keluarga sedang tidak baik-baik saja. Rasanya sulit untuk berkomunikasi dengan mereka.",
                "Keluarga", "Sad", System.currentTimeMillis() - 7200000));

        executorService.execute(() -> {
            if (!isAdded()) return;
            databaseManager.savePosts(samplePosts);

            mainHandler.post(() -> {
                postList.addAll(samplePosts);
                postAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onPostClick(Post post) {
        Intent intent = new Intent(requireContext(), PostDetailActivity.class);
        intent.putExtra("POST_ID", post.getId());
        startActivity(intent);
    }

    @Override
    public void onSupportClick(Post post) {
        // --- Operasi Dukungan Sepenuhnya Lokal ---
        // Karena Anda tidak ingin ada interaksi API untuk dukungan
        post.setSupportCount(post.getSupportCount() + 1);
        executorService.execute(() -> {
            if (isAdded()) {
                databaseManager.updatePost(post);
            }
        });
        mainHandler.post(() -> {
            postAdapter.updatePost(post);
            Toast.makeText(requireContext(), "Dukungan diberikan! 💙", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}