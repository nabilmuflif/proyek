package com.example.curhatku.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.curhatku.R;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.Journal;
import com.example.curhatku.utils.SessionManager;
import com.example.curhatku.utils.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateJournalFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSave;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_journal, container, false);

        editTextTitle = view.findViewById(R.id.editTextJournalTitle);
        editTextContent = view.findViewById(R.id.editTextJournalContent);
        buttonSave = view.findViewById(R.id.buttonSaveJournal);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        databaseManager = DatabaseManager.getInstance(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());

        buttonSave.setOnClickListener(v -> saveJournal());

        return view;
    }

    private void saveJournal() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Catatan harian tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String journalId = UUID.randomUUID().toString();
        String currentUserId = sessionManager.getLoggedInUserId();
        if (currentUserId == null) {
            currentUserId = Constants.DEFAULT_ANONYMOUS_USER_ID;
        }

        long dateTimestamp = Calendar.getInstance().getTimeInMillis();
        long creationTimestamp = System.currentTimeMillis();

        Journal journal = new Journal(journalId, currentUserId, dateTimestamp, title, content, creationTimestamp);

        executorService.execute(() -> {
            databaseManager.saveJournal(journal);
            mainHandler.post(() -> {
                Toast.makeText(requireContext(), "Catatan harian berhasil disimpan!", Toast.LENGTH_SHORT).show();
                editTextTitle.setText("");
                editTextContent.setText("");

                NavController navController = NavHostFragment.findNavController(this);
                navController.navigateUp();
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
