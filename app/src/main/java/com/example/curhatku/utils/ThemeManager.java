package com.example.curhatku.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import static com.example.curhatku.utils.Constants.KEY_THEME;
import static com.example.curhatku.utils.Constants.PREFS_NAME_THEME;

public class ThemeManager {
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME_THEME, Context.MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean(KEY_THEME, false);

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void toggleTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME_THEME, Context.MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean(KEY_THEME, false);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_THEME, !isDarkTheme);
        editor.apply();

        applyTheme(context); // Panggil applyTheme setelah mengubah preferensi
    }

    public static boolean isDarkTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME_THEME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_THEME, false);
    }
}