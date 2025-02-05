package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADD_EXPENSE;
import static com.unimib.triptales.util.Constants.BUDGET;
import static com.unimib.triptales.util.Constants.CATEGORIES;
import static com.unimib.triptales.util.Constants.CURRENCIES;
import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.CURRENCY_GBP;
import static com.unimib.triptales.util.Constants.CURRENCY_JPY;
import static com.unimib.triptales.util.Constants.CURRENCY_USD;
import static com.unimib.triptales.util.Constants.EDIT_EXPENSE;
import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.UPDATED;
import static com.unimib.triptales.util.Constants.FILTER;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ExpensesRecyclerAdapter;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import java.util.List;


public class ExpensesFragment extends Fragment {

    private int budget;
    private double amountSpent;
    private TextView progressTextView;
    private String inputBudget;
    private ImageButton editBudgetButton;
    private LinearProgressIndicator progressIndicator;
    private TextView budgetTextView;
    private EditText numberEditText;
    private FloatingActionButton addExpenseButton;
    private EditText amountEditText;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private EditText descriptionEditText;
    private EditText dayEditText;
    private EditText monthEditText;
    private EditText yearEditText;
    private ConstraintLayout expenseRootLayout;
    private FloatingActionButton editExpenseButton;
    private FloatingActionButton deleteExpenseButton;
    private View overlay_add_edit_expense;
    private View overlay_add_budget;
    private Button filterButton;
    private ImageButton closeFilterButton;
    private TextView totExpenseTextView;
    private TextView filterTextView;
    private View overlay_filter;
    private AutoCompleteTextView filterCategoryEditText;
    private String inputCurrency;
    private AutoCompleteTextView currencyAutoCompleteTextView;
    private TextView noExpensesTextView;
    private List<Expense> expenseList;
    private ExpenseViewModel expenseViewModel;
    private ExpensesRecyclerAdapter expensesRecyclerAdapter;
    private String inputFilterCategory;
    private boolean bEdit;
    private boolean bAdd;
    // serve per il budget non cancellare!
    private TextInputLayout currencyTextInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);
        IExpenseRepository expenseRepository = ServiceLocator.getINSTANCE().getExpenseRepository(getContext());
        expenseViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(expenseRepository)).get(ExpenseViewModel.class);

        expenseViewModel.deselectAllExpenses();

        RecyclerView recyclerViewExpenses = rootView.findViewById(R.id.recyclerViewExpenses);
        expensesRecyclerAdapter = new ExpensesRecyclerAdapter(getContext());
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewExpenses.setAdapter(expensesRecyclerAdapter);

        expensesRecyclerAdapter.setOnExpenseClickListener((expense) -> {
            expenseViewModel.toggleExpenseSelection(expense);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        expenseRootLayout = view.findViewById(R.id.rootLayoutSpese);
        noExpensesTextView = view.findViewById(R.id.noSpeseString);
        bEdit = false;
        bAdd = false;

        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            expenseList = expenses;
            expensesRecyclerAdapter.setExpenseList(expenses);
            if(expenses.isEmpty()){
                noExpensesTextView.setVisibility(View.VISIBLE);
                // da aggiungere quando viene salvato il budget
                /*currencyTextInputLayout.setEnabled(true);*/
            } else {
                noExpensesTextView.setVisibility(View.GONE);
                // da aggiungere quando viene salvato il budget
                /*currencyTextInputLayout.setEnabled(false);
                currencyTextInputLayout.setBoxBackgroundColor
                        (ContextCompat.getColor(requireContext(), R.color.background_overlays));*/
            }
        });

        // gestione del budget
        overlay_add_budget = inflater.inflate(R.layout.overlay_add_budget, expenseRootLayout, false);
        expenseRootLayout.addView(overlay_add_budget);
        overlay_add_budget.setVisibility(View.GONE);
        currencyTextInputLayout = view.findViewById(R.id.textFieldCurrency);

        Button saveBudgetButton = view.findViewById(R.id.salvaBudget);
        editBudgetButton = view.findViewById(R.id.editBudget);
        ImageButton budgetBackButton = view.findViewById(R.id.backButtonBudget);

        editBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseViewModel.setBudgetOverlayVisibility(true);
            }
        });

        budgetBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseViewModel.setBudgetOverlayVisibility(false);
            }
        });

        progressTextView = view.findViewById(R.id.progressText);
        numberEditText = view.findViewById(R.id.inputBudget);
        currencyAutoCompleteTextView = view.findViewById(R.id.inputCurrency);
        budgetTextView = view.findViewById(R.id.totBudget);
        progressIndicator = view.findViewById(R.id.budgetProgressIndicator);
        totExpenseTextView = view.findViewById(R.id.totSpesa);

        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CURRENCIES);
        currencyAutoCompleteTextView.setAdapter(budgetAdapter);

        saveBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputBudget = numberEditText.getText().toString().trim();
                inputCurrency = currencyAutoCompleteTextView.getText().toString().trim();
                boolean correct = expenseViewModel.validateInputBudget(inputBudget, inputCurrency);
                if (correct) {
                    budget = Integer.parseInt(inputBudget);
                    budgetTextView.setText(expenseViewModel.generateTextAmount(inputBudget, inputCurrency));
                    // aggiungere un MutableLiveData<int> budgetLiveData a DiaryViewModel e
                    // poi gestire .observe aggiornando il progress indicator
                    // si può mettere nell'.observe anche tutto questo codice (in teoria)
                    updateProgressIndicator(amountSpent, budget, 0);
                    expenseViewModel.setBudgetOverlayVisibility(false);
                    if(totExpenseTextView.getVisibility() == View.VISIBLE) {
                        expenseViewModel.filterExpenses(inputFilterCategory, inputCurrency);
                    }
                    updateCurrencyIcon();
                }
            }
        });

        expenseViewModel.getBudgetOverlayVisibility().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                if (visible) {
                    disableSwipeAndButtons();
                    showOverlay(BUDGET);
                } else {
                    enableSwipeAndButtons(view);
                    hideOverlay(BUDGET);
                }
            }
        });

        expenseViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if(errorMessage != null){
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //gestione di una spesa
        overlay_add_edit_expense = inflater.inflate(R.layout.overlay_add_edit_expense, expenseRootLayout, false);
        expenseRootLayout.addView(overlay_add_edit_expense);
        overlay_add_edit_expense.setVisibility(View.GONE);
        addExpenseButton = view.findViewById(R.id.addButtonSpese);
        editExpenseButton = view.findViewById(R.id.modificaSpesa);
        deleteExpenseButton = view.findViewById(R.id.eliminaSpesa);

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bAdd = true;
                expenseViewModel.setExpenseOverlayVisibility(true);
            }
        });

        ImageButton expenseBackButton = view.findViewById(R.id.backSpesaButton);

        expenseBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bEdit){
                    editExpenseButton.setVisibility(View.VISIBLE);
                    deleteExpenseButton.setVisibility(View.VISIBLE);
                }
                expenseViewModel.setExpenseOverlayVisibility(false);
            }
        });

        Button saveExpenseButton = view.findViewById(R.id.salvaSpesa);
        amountEditText = view.findViewById(R.id.inputQuantitaSpesa);
        categoryAutoCompleteTextView = view.findViewById(R.id.inputCategory);
        descriptionEditText = view.findViewById(R.id.inputDescription);
        dayEditText = view.findViewById(R.id.inputDay);
        monthEditText = view.findViewById(R.id.inputMonth);
        yearEditText = view.findViewById(R.id.inputYear);

        expenseViewModel.getExpenseOverlayVisibility().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                if (visible) {
                    disableSwipeAndButtons();
                    if(bAdd) {
                        showOverlay(ADD_EXPENSE);
                    } else if(bEdit){
                        showOverlay(EDIT_EXPENSE);
                        editExpenseButton.setVisibility(View.GONE);
                        deleteExpenseButton.setVisibility(View.GONE);
                    }
                } else {
                    enableSwipeAndButtons(view);
                    if(bAdd){
                        hideOverlay(ADD_EXPENSE);
                    } else if(bEdit){
                        hideOverlay(EDIT_EXPENSE);
                    }
                }
            }
        });

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

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        categoryAutoCompleteTextView.setAdapter(categoryAdapter);

        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputAmount = amountEditText.getText().toString().trim();
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
                                inputCurrency, getContext());
                    } else if(bEdit){
                        List<Expense> selectedExpenses = expenseViewModel.getSelectedExpensesLiveData().getValue();
                        if (selectedExpenses != null && !selectedExpenses.isEmpty()) {
                            Expense currentExpense = selectedExpenses.get(0);
                            expenseViewModel.updateExpense(currentExpense, inputCurrency, inputAmount,
                                    inputCategory, inputDescription, inputDay, inputMonth, inputYear);
                            expenseViewModel.deselectAllExpenses();
                        }
                    }
                    expenseViewModel.setExpenseOverlayVisibility(false);
                }
            }
        });

        expenseViewModel.getSelectedExpensesLiveData().observe(getViewLifecycleOwner(), selectedExpenses -> {
            if(selectedExpenses != null) {
                if (selectedExpenses.size() == 1) {
                    if(overlay_add_edit_expense.getVisibility() == View.VISIBLE){
                        editExpenseButton.setVisibility(View.GONE);
                        deleteExpenseButton.setVisibility(View.GONE);
                    } else {
                        addExpenseButton.setEnabled(false);
                        editExpenseButton.setVisibility(View.VISIBLE);
                        deleteExpenseButton.setVisibility(View.VISIBLE);
                    }
                } else if (selectedExpenses.size() == 2) {
                    addExpenseButton.setEnabled(false);
                    editExpenseButton.setVisibility(View.GONE);
                } else if (selectedExpenses.isEmpty()) {
                    editExpenseButton.setVisibility(View.GONE);
                    deleteExpenseButton.setVisibility(View.GONE);
                    addExpenseButton.setEnabled(true);
                }
            }
        });

        expenseViewModel.getExpenseEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if(message != null){
                    switch (message) {
                        case ADDED:
                            Toast.makeText(requireActivity(), R.string.snackbarExpenseAdded, Toast.LENGTH_SHORT).show();
                            break;
                        case UPDATED:
                            Toast.makeText(requireActivity(), R.string.snackbarExpenseUpdated, Toast.LENGTH_SHORT).show();
                            break;
                        case DELETED:
                            Toast.makeText(requireActivity(), R.string.snackbarExpenseDeleted, Toast.LENGTH_SHORT).show();
                            break;
                        case INVALID_DELETE:
                            Toast.makeText(requireActivity(), R.string.snackbarExpenseNotDeleted, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        deleteExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseViewModel.deleteSelectedExpenses(inputCurrency);
            }
        });

        editExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bEdit = true;
                expenseViewModel.setExpenseOverlayVisibility(true);
            }
        });

        // gestione del filtro delle spese
        overlay_filter = inflater.inflate(R.layout.overlay_filter, expenseRootLayout, false);
        expenseRootLayout.addView(overlay_filter);
        overlay_filter.setVisibility(View.GONE);

        filterButton = view.findViewById(R.id.buttonFilter);
        ImageButton filterBackButton = view.findViewById(R.id.backButtonFilter);
        Button saveCategoryButton = view.findViewById(R.id.saveCategory);
        closeFilterButton = view.findViewById(R.id.closeFilter);
        filterTextView = view.findViewById(R.id.testoFiltro);
        filterCategoryEditText = view.findViewById(R.id.inputCategoryFilter);
        filterCategoryEditText.setAdapter(categoryAdapter);

        filterBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseViewModel.setFilterOverlayVisibility(false);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseViewModel.setFilterOverlayVisibility(true);
            }
        });

        expenseViewModel.getFilterOverlayVisibility().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                if(visible){
                    disableSwipeAndButtons();
                    showOverlay(FILTER);
                } else {
                    enableSwipeAndButtons(view);
                    hideOverlay(FILTER);
                }
            }
        });

        expenseViewModel.getFilteredExpensesLiveData().observe(getViewLifecycleOwner(), filteredExpenses -> {
            if (filteredExpenses != null) {
                expensesRecyclerAdapter.setExpenseList(filteredExpenses);
                String tmp = String.valueOf(expenseViewModel.countAmount(filteredExpenses, inputCurrency));
                String totalAmountText = expenseViewModel.generateTextAmount(tmp, inputCurrency);
                totExpenseTextView.setText(totalAmountText);
            }
        });

        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputFilterCategory = filterCategoryEditText.getText().toString().trim();
                expenseViewModel.filterExpenses(inputFilterCategory, inputCurrency);
                expenseViewModel.setFilterOverlayVisibility(false);
                closeFilterButton.setVisibility(View.VISIBLE);
                filterTextView.setVisibility(View.VISIBLE);
                totExpenseTextView.setVisibility(View.VISIBLE);
                addExpenseButton.setEnabled(false);
            }
        });

        closeFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // da gestire expenseList, sarebbe da togliere? da dove prendo gli elementi?
                expensesRecyclerAdapter.setExpenseList(expenseList);
                closeFilterButton.setVisibility(View.GONE);
                filterTextView.setVisibility(View.GONE);
                totExpenseTextView.setVisibility(View.GONE);
                addExpenseButton.setEnabled(true);
            }
        });

        // gestione modifica progress indicator
        expenseViewModel.getAmountSpentLiveData().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double spent) {
                int progressPercentage = (int) ((spent / (float) budget) * 100);
                if(progressPercentage > 100)
                    progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.error));
                else
                    progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.secondary));
                progressIndicator.setProgress(progressPercentage);
                String formattedText = spent + " / " + budget + " spesi" + " (" + progressPercentage + "%)";
                progressTextView.setText(formattedText);
            }
        });
    }

    public void updateProgressIndicator(double expense, int budget, int add){
        if(add == 1)
            amountSpent = amountSpent + expense;
        int progressPercentage = (int) ((amountSpent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.error));
        else
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.secondary));
        progressIndicator.setProgress(progressPercentage);
        String formattedText = amountSpent + " / " + budget + " spesi" + " (" + progressPercentage + "%)";
        progressTextView.setText(formattedText);
    }

    public void updateCurrencyIcon(){
        TextInputLayout textQuantity = overlay_add_edit_expense.findViewById(R.id.textFieldQuantita);
        if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
        }else if(inputCurrency.equalsIgnoreCase(CURRENCY_USD)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
        }else if(inputCurrency.equalsIgnoreCase(CURRENCY_GBP)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
        }else if(inputCurrency.equalsIgnoreCase(CURRENCY_JPY)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
        }
    }

    private void showOverlay(String overlayType) {
        disableSwipeAndButtons();
        switch (overlayType) {
            case BUDGET:
                overlay_add_budget.setVisibility(View.VISIBLE);
                break;
            case ADD_EXPENSE:
                if(budget == 0) {
                    Snackbar snackbar = Snackbar.make(expenseRootLayout, R.string.snackbarErroreBudget, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    expenseViewModel.setExpenseOverlayVisibility(false);
                } else {
                    overlay_add_edit_expense.setVisibility(View.VISIBLE);
                    resetExpenseInputFields();
                }
                break;
            case EDIT_EXPENSE:
                overlay_add_edit_expense.setVisibility(View.VISIBLE);
                populateExpenseFields();
                break;
            case FILTER:
                overlay_filter.setVisibility(View.VISIBLE);
                filterCategoryEditText.setText("", false);
                break;
        }
    }

    private void disableSwipeAndButtons() {
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
        addExpenseButton.setVisibility(View.GONE);
        editBudgetButton.setEnabled(false);
        filterButton.setEnabled(false);
    }

    private void enableSwipeAndButtons(View view) {
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
        Constants.hideKeyboard(view, requireActivity());
        addExpenseButton.setVisibility(View.VISIBLE);
        editBudgetButton.setEnabled(true);
        filterButton.setEnabled(true);
    }

    private void hideOverlay(String overlayType) {
        switch (overlayType) {
            case BUDGET:
                overlay_add_budget.setVisibility(View.GONE);
                break;
            case ADD_EXPENSE:
                overlay_add_edit_expense.setVisibility(View.GONE);
                bAdd = false;
                break;
            case EDIT_EXPENSE:
                overlay_add_edit_expense.setVisibility(View.GONE);
                bEdit = false;
                break;
            case FILTER:
                overlay_filter.setVisibility(View.GONE);
                break;
        }
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
            String tmp = expenseViewModel.extractRealAmount(currentExpense, inputCurrency);
            amountEditText.setText(tmp);
            categoryAutoCompleteTextView.setText(currentExpense.getCategory(), false);
            descriptionEditText.setText(currentExpense.getDescription());

            int[] extractedDate = expenseViewModel.extractDayMonthYear(currentExpense.getDate());
            dayEditText.setText(String.valueOf(extractedDate[0]));
            monthEditText.setText(String.valueOf(extractedDate[1]));
            yearEditText.setText(String.valueOf(extractedDate[2]));
        }
    }
}
