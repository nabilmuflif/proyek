package com.example.curhatku.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.curhatku.R;
import com.example.curhatku.database.DatabaseManager;
import com.example.curhatku.models.User;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnRegister;
    private DatabaseManager databaseManager;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialization
        databaseManager = DatabaseManager.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // UI Binding
        etUsername = findViewById(R.id.et_register_username);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);

        // Register button listener
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required for registration", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            // Check if user already exists
            User existingUser = databaseManager.getUserByUsername(username);
            if (existingUser != null) {
                mainHandler.post(() -> Toast.makeText(this, "Username already exists. Please login.", Toast.LENGTH_SHORT).show());
                return;
            }

            // Create new user
            String newUserId = UUID.randomUUID().toString();
            // In a real app, hash the password before saving!
            User newUser = new User(newUserId, username, email, password, System.currentTimeMillis());
            databaseManager.saveUser(newUser);

            mainHandler.post(() -> {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                finish(); // Close RegisterActivity and return to LoginActivity
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
