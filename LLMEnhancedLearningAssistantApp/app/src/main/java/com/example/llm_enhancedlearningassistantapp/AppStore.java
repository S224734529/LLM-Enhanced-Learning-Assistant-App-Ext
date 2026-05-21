package com.example.llm_enhancedlearningassistantapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class AppStore {
    private static final String PREFS = "learning_app_prefs";

    public static void saveUser(Context context, String username, String email, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public static boolean login(Context context, String username, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String savedUser = prefs.getString("username", "student");
        String savedPassword = prefs.getString("password", "1234");
        return username.equals(savedUser) && password.equals(savedPassword);
    }

    public static String getUsername(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("username", "Student");
    }

    public static void saveInterests(Context context, Set<String> interests) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putStringSet("interests", new HashSet<>(interests))
                .apply();
    }

    public static Set<String> getInterests(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getStringSet("interests", new HashSet<String>());
    }

    public static void saveLastResult(Context context, String result) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString("lastResult", result)
                .apply();
    }

    public static String getLastResult(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString("lastResult", "No result yet.");
    }

    public static void savePlan(Context context, String planName) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString("planName", planName)
                .apply();
    }

    public static String getPlan(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString("planName", "Free");
    }
}