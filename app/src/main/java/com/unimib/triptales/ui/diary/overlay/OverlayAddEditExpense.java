package com.unimib.triptales.ui.diary.overlay;

import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.CURRENCY_GBP;
import static com.unimib.triptales.util.Constants.CURRENCY_JPY;
import static com.unimib.triptales.util.Constants.CURRENCY_USD;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;

import java.util.Arrays;
import java.util.List;

public class OverlayAddEditExpense {
    private final Context context;
    private final ViewGroup rootLayout;
    private final ExpenseViewModel expenseViewModel;
    private final View overlayView;
    private EditText amountEditText;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private EditText descriptionEditText;
    private EditText dayEditText;
    private EditText monthEditText;
    private EditText yearEditText;

    public OverlayAddEditExpense(ViewGroup rootLayout, Context context, ExpenseViewModel expenseViewModel) {
        this.rootLayout = rootLayout;
        this.context = context;
        this.expenseViewModel = expenseViewModel;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_add_edit_expense, rootLayout, false);
    }

    public void showOverlay(boolean bAdd, boolean bEdit, String inputCurrency){
        ImageButton expenseBackButton = overlayView.findViewById(R.id.backSpesaButton);

        expenseBackButton.setOnClickListener(expenseBackButtonListener -> {
                expenseViewModel.setExpenseOverlayVisibility(false);
                hideOverlay();
        });

        Button saveExpenseButton = overlayView.findViewById(R.id.salvaSpesa);
        amountEditText = overlayView.findViewById(R.id.inputQuantitaSpesa);
        amountEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
        categoryAutoCompleteTextView = overlayView.findViewById(R.id.inputCategory);
        descriptionEditText = overlayView.findViewById(R.id.inputDescription);
        dayEditText = overlayView.findViewById(R.id.inputDay);
        monthEditText = overlayView.findViewById(R.id.inputMonth);
        yearEditText = overlayView.findViewById(R.id.inputYear);
        updateCurrencyIcon(inputCurrency);

        if(bEdit){
            populateExpenseFields();
        } else if(bAdd){
            resetExpenseInputFields();
        }

        dayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
                    monthEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        monthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
                    yearEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        List<String> categories = Arrays.asList(context.getString(R.string.shopping_category),
                context.getString(R.string.food_category),
                context.getString(R.string.transport_category),
                context.getString(R.string.accommodation_category),
                context.getString(R.string.culture_category),
                context.getString(R.string.fun_category));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, categories);
        categoryAutoCompleteTextView.setAdapter(categoryAdapter);

        saveExpenseButton.setOnClickListener(saveExpenseButtonListener -> {
            String inputAmount = amountEditText.getText().toString().replace(",", ".");
            String inputCategory = categoryAutoCompleteTextView.getText().toString().trim();
            String inputDescription = descriptionEditText.getText().toString().trim();
            String inputDay = dayEditText.getText().toString().trim();
            String inputMonth = monthEditText.getText().toString().trim();
            String inputYear = yearEditText.getText().toString().trim();

            boolean correct = expenseViewModel.validateInputExpense(inputAmount, inputCategory,
                    inputDescription, inputDay, inputMonth, inputYear);

            if(correct){
                if(bAdd){
                    expenseViewModel.insertExpense(inputAmount, inputCategory,
                            inputDescription, inputDay, inputMonth, inputYear,
                            inputCurrency, context);
                } else if(bEdit){
                    List<Expense> selectedExpenses = expenseViewModel
                            .getSelectedExpensesLiveData().getValue();
                    if (selectedExpenses != null && !selectedExpenses.isEmpty()) {
                        Expense currentExpense = selectedExpenses.get(0);
                        expenseViewModel.updateExpense(currentExpense, inputCurrency, inputAmount,
                                inputCategory, inputDescription, inputDay, inputMonth, inputYear);
                        expenseViewModel.deselectAllExpenses();
                    }
                }
                expenseViewModel.setExpenseOverlayVisibility(false);
                hideOverlay();
            }
        });
        rootLayout.addView(overlayView);
    }

    public void hideOverlay(){
        rootLayout.removeView(overlayView);
    }

    private void resetExpenseInputFields() {
        amountEditText.setText("");
        categoryAutoCompleteTextView.setText("");
        descriptionEditText.setText("");
        dayEditText.setText("");
        monthEditText.setText("");
        yearEditText.setText("");
    }

    private void populateExpenseFields() {
        List<Expense> selectedExpenses = expenseViewModel.getSelectedExpensesLiveData().getValue();
        if (selectedExpenses != null && !selectedExpenses.isEmpty()) {
            Expense currentExpense = selectedExpenses.get(0);
            String tmp = expenseViewModel.extractRealAmount(currentExpense);
            amountEditText.setText(tmp);
            categoryAutoCompleteTextView.setText(currentExpense.getCategory(), false);
            descriptionEditText.setText(currentExpense.getDescription());

            int[] extractedDate = expenseViewModel.extractDayMonthYear(currentExpense.getDate());
            dayEditText.setText(String.valueOf(extractedDate[0]));
            monthEditText.setText(String.valueOf(extractedDate[1]));
            yearEditText.setText(String.valueOf(extractedDate[2]));
        }
    }

    public void updateCurrencyIcon(String currency){
        TextInputLayout textQuantity = overlayView.findViewById(R.id.textFieldQuantita);
        if(currency.equalsIgnoreCase(CURRENCY_EUR)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
        }else if(currency.equalsIgnoreCase(CURRENCY_USD)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
        }else if(currency.equalsIgnoreCase(CURRENCY_GBP)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
        }else if(currency.equalsIgnoreCase(CURRENCY_JPY)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
        }
    }
}
