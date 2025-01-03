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

    public static final String CURRENCY_EUR = "€";
    public static final String CURRENCY_USD = "$";
    public static final String CURRENCY_GBP = "£";
    public static final String CURRENCY_JPY = "¥";
    public static final int MINIMUM_LENGTH_PASSWORD = 8;

    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";

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

    public static MaterialCardView findSelectedCard(ArrayList<MaterialCardView> cardList){
        MaterialCardView selectedCard = cardList.get(0);
        for (MaterialCardView card : cardList) {
            if (card.isSelected())
                selectedCard = card;
        }
        return selectedCard;
    }

    public static String countAmount(List<Expense> expenseList, String currency){
        double totExpense = 0;
        for(Expense e: expenseList){
            String amount = e.getAmount();
            String realAmount;
            if(currency.equalsIgnoreCase(CURRENCY_EUR)){
                realAmount = amount.substring(0, amount.length()-1);
            } else {
                realAmount = amount.substring(1);
            }
            totExpense += Double.parseDouble(realAmount);
        }
        String tmp;
        if(currency.equalsIgnoreCase(CURRENCY_EUR))
            tmp = totExpense+currency;
        else
            tmp = currency+totExpense;

        return tmp;
    }


}
