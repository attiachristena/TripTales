package com.unimib.triptales.util;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.unimib.triptales.model.Expense;

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

    public static void hideKeyboard(View view, FragmentActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //conta le spese selezionate
    public static int countSelectedCards(List<Expense> expenseList) {
        int selectedCount = 0;
        for (Expense e : expenseList) {
            if (e.isSelected())
                selectedCount++;
        }
        return selectedCount;  // Restituisce il numero di card selezionate
    }

    //ritorna la card selezionata
    public static MaterialCardView findSelectedCard(ArrayList<MaterialCardView> cardList){
        MaterialCardView selectedCard = cardList.get(0);
        for (MaterialCardView card : cardList) {
            if (card.isSelected())
                selectedCard = card;
        }
        return selectedCard;
    }

}
