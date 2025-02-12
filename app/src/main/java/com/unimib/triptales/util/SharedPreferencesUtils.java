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
    private static final String KEY_FIRST_ACCESS = "firstAccess";
    private static final String PREFS_NAME = "TriptalesPrefs";
    private static final String CHECKPOINT_DIARY_ID_KEY = "checkpoint_diary_id";
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

    public static boolean isFirstAccess(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_FIRST_ACCESS, true); // Default: true se non esiste
    }

    // Metodo per impostare il flag dopo il primo accesso
    public static void setFirstAccessComplete(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_FIRST_ACCESS, false); // Imposta il flag come "non è il primo accesso"
        editor.apply();
    }

    // Salva il diaryId nelle SharedPreferences
    public static void saveDiaryId(Context context, String diaryId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DIARY_ID, diaryId);
        editor.apply();
    }

    public static void saveCheckpointDiaryId(Context context, int id) {
        SharedPreferences prefs = context.getSharedPreferences("TripTales", Context.MODE_PRIVATE);
        prefs.edit().putInt(CHECKPOINT_DIARY_ID_KEY, id).apply();
        Log.d("SharedPreferencesUtils", "Saved checkpoint diary ID: " + id);
    }

    public static void setCheckpointDiaryId(Context context, int checkpointDiaryId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CHECKPOINT_DIARY_ID", checkpointDiaryId);
        editor.apply();

        Log.d("SharedPreferences", "Checkpoint Diary ID salvato: " + checkpointDiaryId);
    }



    public static int getCheckpointDiaryId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("YourAppPrefs", Context.MODE_PRIVATE);
        int checkpointDiaryId = sharedPreferences.getInt("CHECKPOINT_DIARY_ID", 0);
        Log.d("SharedPreferences", "Checkpoint Diary ID recuperato: " + checkpointDiaryId);
        return checkpointDiaryId;
    }



    // Recupera il diaryId salvato
    public static String getDiaryId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_DIARY_ID, null); // Ritorna null se non esiste
    }

    public static void saveImageCardItemId(Context context, int imageCardItemId) {
        SharedPreferences prefs = context.getSharedPreferences("TripTales", Context.MODE_PRIVATE);
        prefs.edit().putInt("imageCardItemId", imageCardItemId).apply();
    }



    // Rimuove il diaryId (es. logout)
    public static void clearDiaryId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_DIARY_ID);
        editor.apply();
    }

    public static String getLoggedUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        return firebaseUser.getUid();
    }

}
