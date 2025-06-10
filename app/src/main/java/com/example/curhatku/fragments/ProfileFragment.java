package com.example.curhatku.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText; // Untuk dialog edit
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.curhatku.activities.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.example.curhatku.R;
import com.example.curhatku.utils.ThemeManager;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.network.NetworkManager;
import com.example.curhatku.models.Quote;
import com.example.curhatku.models.User; // Import User model
import com.example.curhatku.utils.SessionManager;
import android.content.Intent;
import static com.example.curhatku.utils.Constants.PREFS_NAME_USER_STATS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView tvProfileUsername; // Tambah
    private TextView tvProfileEmail;    // Tambah
    private MaterialButton btnEditProfile; // Tambah
    private MaterialButton btnChangePassword; // Tambah

    private TextView tvQuoteText;
    private TextView tvQuoteAuthor;
    private SwitchMaterial switchDarkMode;
    private MaterialButton btnRefreshQuote;
    private MaterialButton btnClearData;
    private MaterialButton btnAbout;
    private MaterialButton btnLogout;
    private MaterialCardView cardQuote;
    private CircularProgressIndicator progressQuote;

    private ExecutorService executorService;
    private Handler mainHandler;
    private NetworkManager networkManager;
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    private SharedPreferences userPrefs;
    private User currentUser; // Untuk menyimpan data user yang sedang login

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupClickListeners();

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        networkManager = NetworkManager.getInstance(requireContext());
        databaseManager = DatabaseManager.getInstance(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());
        userPrefs = requireContext().getSharedPreferences(PREFS_NAME_USER_STATS, Context.MODE_PRIVATE);

        loadUserProfile(); // Memuat data profil pengguna
        loadRandomQuote();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // Refresh profil setiap kali fragment di-resume
        // loadUserStats(); // loadUserStats sekarang kosong, jadi ini bisa diabaikan
    }

    private void initViews(View view) {
        tvProfileUsername = view.findViewById(R.id.tv_profile_username); // Inisialisasi
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);       // Inisialisasi
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);       // Inisialisasi
        btnChangePassword = view.findViewById(R.id.btn_change_password); // Inisialisasi

        tvQuoteText = view.findViewById(R.id.tv_quote_text);
        tvQuoteAuthor = view.findViewById(R.id.tv_quote_author);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        btnRefreshQuote = view.findViewById(R.id.btn_refresh_quote);
        btnClearData = view.findViewById(R.id.btn_clear_data);
        btnAbout = view.findViewById(R.id.btn_about);
        btnLogout = view.findViewById(R.id.btn_logout);
        cardQuote = view.findViewById(R.id.card_quote);
        progressQuote = view.findViewById(R.id.progress_quote);

        switchDarkMode.setChecked(ThemeManager.isDarkTheme(requireContext()));
    }

    private void setupClickListeners() {
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeManager.toggleTheme(requireContext());
            requireActivity().recreate();
        });
        btnRefreshQuote.setOnClickListener(v -> loadRandomQuote());

        btnClearData.setOnClickListener(v -> showClearDataDialog());

        btnAbout.setOnClickListener(v -> showAboutDialog());

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // --- Listener Baru untuk Edit Profil dan Ganti Password ---
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        // --- Akhir Listener Baru ---
    }

    private void loadUserProfile() {
        String userId = sessionManager.getLoggedInUserId();
        if (userId != null && !userId.equals(com.example.curhatku.utils.Constants.DEFAULT_ANONYMOUS_USER_ID)) {
            executorService.execute(() -> {
                currentUser = databaseManager.getUserById(userId); // Pastikan getUserById di DatabaseManager mengembalikan User
                mainHandler.post(() -> {
                    if (currentUser != null) {
                        tvProfileUsername.setText(currentUser.getUsername());
                        tvProfileEmail.setText(currentUser.getEmail());
                    } else {
                        tvProfileUsername.setText("Guest");
                        tvProfileEmail.setText("guest@example.com");
                        Log.w("ProfileFragment", "Logged in user not found in DB: " + userId);
                    }
                });
            });
        } else {
            tvProfileUsername.setText("Guest");
            tvProfileEmail.setText("guest@example.com");
            btnEditProfile.setEnabled(false); // Nonaktifkan jika user tidak login
            btnChangePassword.setEnabled(false); // Nonaktifkan jika user tidak login
        }
    }

    private void showEditProfileDialog() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Tidak ada profil untuk diedit.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null); // Anda perlu membuat layout ini
        EditText etEditUsername = dialogView.findViewById(R.id.et_edit_username);
        EditText etEditEmail = dialogView.findViewById(R.id.et_edit_email);

        etEditUsername.setText(currentUser.getUsername());
        etEditEmail.setText(currentUser.getEmail());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profil")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newUsername = etEditUsername.getText().toString().trim();
                    String newEmail = etEditEmail.getText().toString().trim();

                    if (newUsername.isEmpty()) {
                        Toast.makeText(requireContext(), "Username tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newEmail.isEmpty()) {
                        Toast.makeText(requireContext(), "Email tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Perbarui objek currentUser
                    currentUser.setUsername(newUsername);
                    currentUser.setEmail(newEmail);
                    currentUser.setLastLogin(System.currentTimeMillis()); // Update last login time

                    executorService.execute(() -> {
                        databaseManager.updateUser(currentUser); // Update di DB lokal
                        mainHandler.post(() -> {
                            loadUserProfile(); // Refresh tampilan profil
                            Toast.makeText(requireContext(), "Profil berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                            // Update SessionManager juga jika username berubah
                            sessionManager.createLoginSession(currentUser.getId(), currentUser.getUsername(), sessionManager.getAuthToken());
                        });
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showChangePasswordDialog() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Tidak ada profil untuk ganti password.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null); // Anda perlu membuat layout ini
        EditText etOldPassword = dialogView.findViewById(R.id.et_old_password);
        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        EditText etConfirmNewPassword = dialogView.findViewById(R.id.et_confirm_new_password);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Ganti Password")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString().trim();
                    String newPassword = etNewPassword.getText().toString().trim();
                    String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

                    if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "Semua kolom harus diisi.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Verifikasi password lama
                    if (!currentUser.getPasswordHash().equals(oldPassword)) { // Ingat: ini plain text, tidak disarankan di produksi
                        Toast.makeText(requireContext(), "Password lama salah.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Konfirmasi password baru
                    if (!newPassword.equals(confirmNewPassword)) {
                        Toast.makeText(requireContext(), "Konfirmasi password baru tidak cocok.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update password di objek currentUser
                    currentUser.setPasswordHash(newPassword); // Ingat: ini plain text
                    currentUser.setLastLogin(System.currentTimeMillis()); // Update last login time

                    executorService.execute(() -> {
                        databaseManager.updateUser(currentUser); // Update di DB lokal
                        mainHandler.post(() -> {
                            Toast.makeText(requireContext(), "Password berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void loadUserStats() {
        // Metode ini sekarang kosong karena statistik tidak ditampilkan di UI
    }

    private void loadRandomQuote() {
        progressQuote.setVisibility(View.VISIBLE);
        cardQuote.setVisibility(View.GONE);
        btnRefreshQuote.setEnabled(false);

        networkManager.getRandomQuote(new NetworkManager.NetworkCallback<Quote>() {
            @Override
            public void onSuccess(Quote quote) {
                mainHandler.post(() -> {
                    tvQuoteText.setText("\"" + quote.getContent() + "\"");
                    tvQuoteAuthor.setText("- " + quote.getAuthor());
                    progressQuote.setVisibility(View.GONE);
                    cardQuote.setVisibility(View.VISIBLE);
                    btnRefreshQuote.setEnabled(true);
                });
            }

            @Override
            public void onError(String errorMessage) {
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    tvQuoteText.setText("Gagal memuat kutipan.");
                    tvQuoteAuthor.setText("Tidak ada kutipan.");
                    progressQuote.setVisibility(View.GONE);
                    cardQuote.setVisibility(View.VISIBLE);
                    btnRefreshQuote.setEnabled(true);
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Terjadi kesalahan teknis: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    tvQuoteText.setText("Terjadi kesalahan teknis.");
                    tvQuoteAuthor.setText("Mohon coba lagi.");
                    progressQuote.setVisibility(View.GONE);
                    cardQuote.setVisibility(View.VISIBLE);
                    btnRefreshQuote.setEnabled(true);
                });
            }
        });
    }

    private void showClearDataDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Semua Data")
                .setMessage("Apakah Anda yakin ingin menghapus semua data curhat yang tersimpan? " +
                        "Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> clearAllData())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void clearAllData() {
        executorService.execute(() -> {
            try {
                databaseManager.clearAllData();

                SharedPreferences.Editor editor = userPrefs.edit();
                editor.clear();
                editor.apply();

                sessionManager.logoutUser();

                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Semua data berhasil dihapus",
                            Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("ProfileFragment", "Error clearing all data: " + e.getMessage());
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Gagal menghapus data",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Tentang CurhatKu")
                .setMessage("CurhatKu v1.0\n\n" +
                        "Aplikasi media sosial anonim untuk berbagi curhat dan " +
                        "mendapatkan dukungan dari komunitas.\n\n" +
                        "Fitur:\n" +
                        "• Posting anonim\n" +
                        "• Berbagai kategori curhat\n" +
                        "• Mood tracker\n" +
                        "• Mode gelap/terang\n" +
                        "• Dukungan offline\n\n" +
                        "Dikembangkan untuk Lab Mobile 2025\n" +
                        "Tema: Sosial & Komunikasi")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performLogout() {
        sessionManager.logoutUser();
        Toast.makeText(requireContext(), "Anda telah logout", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}