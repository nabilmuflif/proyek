package com.example.curhatku.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.example.curhatku.utils.Constants.KEY_AUTH_TOKEN;
import static com.example.curhatku.utils.Constants.KEY_CURRENT_USER_ID;
import static com.example.curhatku.utils.Constants.KEY_CURRENT_USERNAME;
import static com.example.curhatku.utils.Constants.KEY_IS_FIRST_TIME_USER;
import static com.example.curhatku.utils.Constants.PREFS_NAME_SESSION;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS_NAME_SESSION, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_AUTH_TOKEN) && prefs.getString(KEY_AUTH_TOKEN, null) != null;
    }

    public void createLoginSession(String userId, String username, String authToken) {
        editor.putString(KEY_CURRENT_USER_ID, userId);
        editor.putString(KEY_CURRENT_USERNAME, username);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putBoolean(KEY_IS_FIRST_TIME_USER, false);
        editor.apply();
        Log.d("SessionManager", "Login session created for user: " + username + " with token: " + authToken);
    }

    public String getLoggedInUserId() {
        return prefs.getString(KEY_CURRENT_USER_ID, Constants.DEFAULT_ANONYMOUS_USER_ID);
    }

    public String getLoggedInUsername() {
        return prefs.getString(KEY_CURRENT_USERNAME, null);
    }

    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
        Log.d("SessionManager", "User logged out. Session cleared.");
    }

    public boolean isFirstTimeUser() {
        return prefs.getBoolean(KEY_IS_FIRST_TIME_USER, true);
    }

    public void setNotFirstTimeUser() {
        editor.putBoolean(KEY_IS_FIRST_TIME_USER, false);
        editor.apply();
    }
}