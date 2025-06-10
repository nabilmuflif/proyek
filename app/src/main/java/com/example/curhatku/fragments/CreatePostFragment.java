package com.example.curhatku.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.curhatku.R;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePostFragment extends Fragment {

    private TextInputEditText etContent;
    private Spinner spinnerCategory;
    private Spinner spinnerMood;
    private MaterialButton btnPost;

    private ExecutorService executorService;
    private Handler mainHandler;
    private DatabaseManager databaseManager; // Tambahkan ini

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etContent = view.findViewById(R.id.et_content);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerMood = view.findViewById(R.id.spinner_mood);
        btnPost = view.findViewById(R.id.btn_post);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        databaseManager = DatabaseManager.getInstance(requireContext()); // Inisialisasi

        setupSpinners();
        setupPostButton();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.post_categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.post_moods,
                android.R.layout.simple_spinner_item
        );
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMood.setAdapter(moodAdapter);
    }

    private void setupPostButton() {
        btnPost.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String mood = spinnerMood.getSelectedItem().toString();

            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Curhat tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            Post newPost = new Post(
                    UUID.randomUUID().toString(),
                    content,
                    category,
                    mood,
                    System.currentTimeMillis()
            );

            executorService.execute(() -> {
                databaseManager.savePost(newPost);
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Curhat berhasil diposting!", Toast.LENGTH_SHORT).show();
                    etContent.setText("");
                    spinnerCategory.setSelection(0);
                    spinnerMood.setSelection(0);

                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    } else {
                        NavHostFragment.findNavController(this).navigate(R.id.homeFragment);
                    }
                });
            });
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