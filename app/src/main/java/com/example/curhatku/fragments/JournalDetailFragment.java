package com.example.curhatku.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.curhatku.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.Journal;
import com.example.curhatku.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalDetailFragment extends Fragment {

    private String journalId;
    private Journal currentJournal;

    private TextView tvDetailJournalTitle;
    private TextView tvDetailJournalDate;
    private TextView tvDetailJournalContent;
    private MaterialButton btnEditJournal;
    private MaterialButton btnDeleteJournal;
    private ImageButton btnBack;

    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            journalId = getArguments().getString("JOURNAL_ID");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_detail, container, false);

        tvDetailJournalTitle = view.findViewById(R.id.tv_detail_journal_title);
        tvDetailJournalDate = view.findViewById(R.id.tv_detail_journal_date);
        tvDetailJournalContent = view.findViewById(R.id.tv_detail_journal_content);
        btnEditJournal = view.findViewById(R.id.btn_edit_journal);
        btnDeleteJournal = view.findViewById(R.id.btn_delete_journal);
        btnBack = view.findViewById(R.id.btn_back_journal_detail);


        databaseManager = DatabaseManager.getInstance(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        setupClickListeners();
        loadJournalDetail();

        return view;
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
        });

        btnEditJournal.setOnClickListener(v -> showEditJournalDialog());
        btnDeleteJournal.setOnClickListener(v -> showDeleteJournalDialog());
    }

    private void loadJournalDetail() {
        if (journalId == null) {
            Toast.makeText(requireContext(), "ID Jurnal tidak ditemukan.", Toast.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
            return;
        }

        executorService.execute(() -> {
            Journal journal = databaseManager.getJournalById(journalId);

            mainHandler.post(() -> {
                if (journal != null) {
                    currentJournal = journal;
                    displayJournalDetails();
                } else {
                    Toast.makeText(requireContext(), "Jurnal tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigateUp();
                }
            });
        });
    }

    private void displayJournalDetails() {
        if (currentJournal == null) return;

        tvDetailJournalTitle.setText(currentJournal.getTitle().isEmpty() ? "(No Title)" : currentJournal.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM, HH:mm", Locale.getDefault());
        tvDetailJournalDate.setText(sdf.format(new Date(currentJournal.getDateTimestamp())));
        tvDetailJournalContent.setText(currentJournal.getContent());
    }

    // --- METODE EDIT JURNAL YANG DIPISAHKAN ---
    private void showEditJournalDialog() {
        if (currentJournal == null) return;

        // Gunakan layout dialog_edit_journal yang baru
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_journal, null);
        EditText etEditTitle = dialogView.findViewById(R.id.et_journal_edit_title);
        EditText etEditContent = dialogView.findViewById(R.id.et_journal_edit_content);

        etEditTitle.setText(currentJournal.getTitle());
        etEditContent.setText(currentJournal.getContent());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Jurnal")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newTitle = etEditTitle.getText().toString().trim();
                    String newContent = etEditContent.getText().toString().trim();

                    if (newContent.isEmpty()) {
                        Toast.makeText(requireContext(), "Konten jurnal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentJournal.setTitle(newTitle);
                    currentJournal.setContent(newContent);
                    currentJournal.setTimestamp(System.currentTimeMillis()); // Update timestamp jurnal terakhir diedit

                    executorService.execute(() -> {
                        // Di SQLite manual, saveJournal dengan ID yang sudah ada akan melakukan UPDATE karena PRIMARY KEY.
                        databaseManager.saveJournal(currentJournal);
                        mainHandler.post(() -> {
                            displayJournalDetails(); // Refresh tampilan detail
                            Toast.makeText(requireContext(), "Jurnal berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }
    // --- AKHIR METODE EDIT JURNAL ---


    private void showDeleteJournalDialog() {
        if (currentJournal == null) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Jurnal?")
                .setMessage("Apakah Anda yakin ingin menghapus catatan jurnal ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteJournal())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteJournal() {
        if (currentJournal == null) return;

        executorService.execute(() -> {
            databaseManager.deleteJournal(currentJournal.getId()); // Hapus dari DB lokal
            mainHandler.post(() -> {
                Toast.makeText(requireContext(), "Jurnal dihapus.", Toast.LENGTH_SHORT).show();
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigateUp(); // Kembali ke daftar jurnal setelah dihapus
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