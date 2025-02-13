package com.unimib.triptales.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

public class SharedPreferencesUtils {
    private static final String PREF_NAME = "TripTalesPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_DIARY_ID = "current_diary_id";
    private static final String KEY_CHECKPOINT_DIARY_ID = "checkpoint_diary_id";
    private final Context context;

    public SharedPreferencesUtils(Context context){
        this.context = context;
    }

    public void writeStringData(String sharedPreferencesFileName, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeStringData(String sharedPreferencesFileName, String key, Set<String> value){
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public String readStringData(String sharedPreferencesFileName, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public Set<String> readStringSetData(String sharedPreferencesFileName, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
        return sharedPref.getStringSet(key, null);
    }

    public static void setLoggedIn(Context context, boolean isLoggedIn) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Salva il diaryId nelle SharedPreferences
    public static void saveDiaryId(Context context, String diaryId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DIARY_ID, diaryId);
        editor.apply();
    }

    // Metodo per salvare l'ID del CheckpointDiary
    public static void saveCheckpointDiaryId(Context context, int id) {
        Log.d("SharedPrefs", "Attempting to save ID: " + id);

        // Validate id before saving
        if (id <= 0) {
            Log.e("SharedPrefs", "Attempted to save invalid ID: " + id);
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CHECKPOINT_DIARY_ID, id);
        boolean success = editor.commit();
        Log.d("SharedPrefs", "Save success: " + success + " for ID: " + id);
    }


    // Metodo per ottenere l'ID del CheckpointDiary salvato
    public static int getCheckpointDiaryId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_CHECKPOINT_DIARY_ID, -1);
    }

    // Recupera il diaryId salvato
    public static String getDiaryId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_DIARY_ID, null); // Ritorna null se non esiste
    }

    public static String getLoggedUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        return firebaseUser.getUid();
    }

    public static String getLoggedUserEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        return firebaseUser.getEmail();
    }

}
