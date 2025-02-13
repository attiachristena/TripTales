package com.unimib.triptales.ui.diary.overlay;

import static com.unimib.triptales.util.Constants.CURRENCIES;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.util.SharedPreferencesUtils;

public class OverlayBudget {
    private final Context context;
    private final ViewGroup rootLayout;
    private final ExpenseViewModel expenseViewModel;
    private final View overlayView;
    private String inputCurrency;

    public OverlayBudget(Context context, ViewGroup rootLayout, String inputCurrency,
                         ExpenseViewModel expenseViewModel) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.inputCurrency = inputCurrency;
        this.expenseViewModel = expenseViewModel;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_add_budget, rootLayout, false);
    }

    public void showOverlay(HomeViewModel homeViewModel){
        Button saveBudgetButton = overlayView.findViewById(R.id.salvaBudget);
        ImageButton budgetBackButton = overlayView.findViewById(R.id.backButtonBudget);
        expenseViewModel.setBudgetOverlayVisibility(true);

        budgetBackButton.setOnClickListener(budgetBackButtonListener -> {
                expenseViewModel.setBudgetOverlayVisibility(false);
                hideOverlay();
        });

        EditText numberEditText = overlayView.findViewById(R.id.inputBudget);
        AutoCompleteTextView currencyAutoCompleteTextView = overlayView.findViewById(R.id.inputCurrency);

        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, CURRENCIES);
        currencyAutoCompleteTextView.setAdapter(budgetAdapter);

        saveBudgetButton.setOnClickListener(saveBudget -> {
            String inputBudget = numberEditText.getText().toString().trim();
            if(inputCurrency == null || !inputCurrency.equals(currencyAutoCompleteTextView.getText().toString().trim())) {
                inputCurrency = currencyAutoCompleteTextView.getText().toString().trim();
            }
            boolean correct = expenseViewModel.validateInputBudget(inputBudget, inputCurrency);
            if (correct) {
                String completeBudget = expenseViewModel.generateTextAmount(inputBudget, inputCurrency);
                homeViewModel.updateDiaryBudget(SharedPreferencesUtils.getDiaryId(context), completeBudget);
                expenseViewModel.setBudgetOverlayVisibility(false);
                hideOverlay();
            }
        });

        rootLayout.addView(overlayView);
    }

    public void hideOverlay(){
        rootLayout.removeView(overlayView);
    }
}
