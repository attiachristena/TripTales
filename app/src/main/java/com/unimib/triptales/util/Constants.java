package com.unimib.triptales.util;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Goal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final int DATABASE_VERSION = 1;
    public static final String APP_DATABASE = "app db";

    public static final String SHOPPING = "Shopping";
    public static final String FOOD = "Cibo";
    public static final String TRANSPORT = "Trasporto";
    public static final String ACCOMMODATION = "Alloggio";
    public static final String CULTURE = "Cultura";
    public static final String RECREATION = "Svago";

    public static final List<String> CATEGORIES = Arrays.asList(SHOPPING,
            FOOD, TRANSPORT, ACCOMMODATION, CULTURE, RECREATION);

    public static final String BUDGET = "BUDGET";
    public static final String ADD_EXPENSE = "ADD_EXPENSE";
    public static final String EDIT_EXPENSE = "EDIT_EXPENSE";
    public static final String FILTER = "FILTER";
    public static final String EXPENSE_ADDED = "EXPENSE_ADDED";
    public static final String EXPENSE_UPDATED = "EXPENSE_UPDATED";
    public static final String EXPENSE_DELETED = "EXPENSE_DELETED";
    public static final String INVALID_DELETE = "INVALID_DELETE";

    public static final String ACTIVE_FRAGMENT_TAG = "active_fragment";

    public static final String CURRENCY_EUR = "€";
    public static final String CURRENCY_USD = "$";
    public static final String CURRENCY_GBP = "£";
    public static final String CURRENCY_JPY = "¥";

    public static final String WEAK_PASSWORD_ERROR = "La password è troppo debole. Per favore, scegli una password più robusta.";
    public static final String INVALID_CREDENTIALS_ERROR = "Credenziali non valide. Per favore, controlla l'email e la password inserite.";
    public static final String INVALID_USER_ERROR = "Utente non trovato. Per favore, registrati prima di accedere.";
    public static final String USER_COLLISION_ERROR = "Esiste già un utente con queste credenziali. Per favore, usa un'email diversa.";
    public static final String MULTI_FACTOR_ERROR = "L'utente è registrato con l'autenticazione a due fattori. Per favore, completala per accedere.";
    public static final String UNEXPECTED_ERROR = "Errore inaspettato. Per favore, riprova.";
    public static final String INVALID_ID_TOKEN = "ID token non valido.";



    public static final String FIREBASE_REALTIME_DATABASE = "https://triptales-4765a-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";

    public static final List<String> CURRENCIES = Arrays.asList(CURRENCY_EUR,
            CURRENCY_USD, CURRENCY_GBP, CURRENCY_JPY);

    public static void hideKeyboard(View view, FragmentActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
