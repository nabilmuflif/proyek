package com.example.curhatku.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.curhatku.R;
import com.example.curhatku.adapters.JournalAdapter;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.Journal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.curhatku.utils.SessionManager;
import com.example.curhatku.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalListFragment extends Fragment implements JournalAdapter.OnJournalClickListener {

    private RecyclerView recyclerView;
    private JournalAdapter journalAdapter;
    private List<Journal> journalList;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddJournal;
    private TextView tvNoJournals;

    private ExecutorService executorService;
    private Handler mainHandler;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_journals);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_journal);
        fabAddJournal = view.findViewById(R.id.fab_add_journal);
        tvNoJournals = view.findViewById(R.id.tv_no_journals);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        databaseManager = DatabaseManager.getInstance(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());

        setupRecyclerView();
        setupClickListeners();
        setupSwipeRefresh();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadJournals();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJournals();
    }

    private void setupRecyclerView() {
        journalList = new ArrayList<>();
        journalAdapter = new JournalAdapter(journalList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(journalAdapter);
    }

    private void setupClickListeners() {
        fabAddJournal.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.createJournalFragment);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadJournals);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
    }

    private void loadJournals() {
        swipeRefresh.setRefreshing(true);
        executorService.execute(() -> {
            if (!isAdded()) return;

            String currentUserId = sessionManager.getLoggedInUserId();
            if (currentUserId == null || currentUserId.equals(Constants.DEFAULT_ANONYMOUS_USER_ID)) {
                Log.w("JournalListFragment", "No logged in user found to load journals. Displaying empty state.");
                mainHandler.post(() -> {
                    journalList.clear();
                    journalAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                    updateEmptyState(true);
                });
                return;
            }

            List<Journal> journals = databaseManager.getAllJournalsByUser(currentUserId);
            mainHandler.post(() -> {
                journalList.clear();
                journalList.addAll(journals);
                journalAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                updateEmptyState(journals.isEmpty());
            });
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            tvNoJournals.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoJournals.setVisibility(View.GONE);
        }
    }

    @Override
    public void onJournalClick(Journal journal) {
        // --- NAVIGASI KE JOURNALDETAILFRAGMENT ---
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("JOURNAL_ID", journal.getId()); // Teruskan ID jurnal sebagai String
        navController.navigate(R.id.action_journalFragment_to_journalDetailFragment, args); // Gunakan ID action yang akan kita buat
        // --- AKHIR NAVIGASI ---
    }

    @Override
    public void onJournalLongClick(Journal journal) {
        Toast.makeText(requireContext(), "Jurnal long-clicked: " + journal.getTitle(), Toast.LENGTH_SHORT).show();
        // Implementasi opsi edit/hapus melalui dialog atau menu konteks
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}