package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADD_EXPENSE;
import static com.unimib.triptales.util.Constants.BUDGET;
import static com.unimib.triptales.util.Constants.CATEGORIES;
import static com.unimib.triptales.util.Constants.CURRENCIES;
import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.CURRENCY_GBP;
import static com.unimib.triptales.util.Constants.CURRENCY_JPY;
import static com.unimib.triptales.util.Constants.CURRENCY_USD;
import static com.unimib.triptales.util.Constants.DELETE_EXPENSE;
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
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.List;


public class ExpensesFragment extends Fragment {

    private int budget;
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
    private ExpenseViewModel expenseViewModel;
    private HomeViewModel homeViewModel;
    private ExpensesRecyclerAdapter expensesRecyclerAdapter;
    private String inputFilterCategory;
    private boolean bEdit;
    private boolean bAdd;
    private boolean bFilter;
    private TextInputLayout currencyTextInputLayout;
    private View overlay_delete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);
        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getContext());
        homeViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(diaryRepository)).get(HomeViewModel.class);
        IExpenseRepository expenseRepository = ServiceLocator.getINSTANCE().getExpenseRepository(getContext());
        expenseViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(expenseRepository)).get(ExpenseViewModel.class);

        progressTextView = rootView.findViewById(R.id.progressText);
        budgetTextView = rootView.findViewById(R.id.totBudget);
        progressIndicator = rootView.findViewById(R.id.budgetProgressIndicator);

        expenseViewModel.deselectAllExpenses();
        expenseViewModel.loadAmountSpent();

        RecyclerView recyclerViewExpenses = rootView.findViewById(R.id.recyclerViewExpenses);
        expensesRecyclerAdapter = new ExpensesRecyclerAdapter(getContext());
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewExpenses.setAdapter(expensesRecyclerAdapter);

        expensesRecyclerAdapter.setOnExpenseClickListener((expense) -> {
            if(!bFilter) {
                expenseViewModel.toggleExpenseSelection(expense);
            }
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
        bFilter = false;

        String tmp = homeViewModel.getBudget(SharedPreferencesUtils.getDiaryId(getContext()));
        if(tmp != null){
            inputCurrency = expenseViewModel.getInputCurrency(tmp);
            if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
                budget = Integer.parseInt(tmp.substring(0, tmp.length()-1));
            else
                budget = Integer.parseInt(tmp.substring(1));
            budgetTextView.setText(tmp);
            double spent;
            if(expenseViewModel.getAmountSpentLiveData().getValue() != null) {
                spent = expenseViewModel.getAmountSpentLiveData().getValue();
            } else {
                spent = 0;
            }
            updateProgressIndicator(spent, budget);
        }

        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            if(expenses != null) {
                expensesRecyclerAdapter.setExpenseList(expenses);
                if (expenses.isEmpty()) {
                    noExpensesTextView.setVisibility(View.VISIBLE);
                    currencyTextInputLayout.setEnabled(true);
                } else {
                    noExpensesTextView.setVisibility(View.GONE);
                    currencyTextInputLayout.setEnabled(false);
                    currencyTextInputLayout.setBoxBackgroundColor
                        (ContextCompat.getColor(requireContext(), R.color.background_overlays));
                }
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

        editBudgetButton.setOnClickListener(view1 ->
                expenseViewModel.setBudgetOverlayVisibility(true));

        budgetBackButton.setOnClickListener(view12 ->
                expenseViewModel.setBudgetOverlayVisibility(false));

        numberEditText = view.findViewById(R.id.inputBudget);
        currencyAutoCompleteTextView = view.findViewById(R.id.inputCurrency);
        totExpenseTextView = view.findViewById(R.id.totSpesa);

        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CURRENCIES);
        currencyAutoCompleteTextView.setAdapter(budgetAdapter);

        homeViewModel.getBudgetLiveData().observe(getViewLifecycleOwner(), diaryBudget -> {
            if(diaryBudget != null) {
                if(inputCurrency.equals(CURRENCY_EUR)){
                    budget = Integer.parseInt(diaryBudget.substring(0, diaryBudget.length() - 1));
                } else {
                    budget = Integer.parseInt(diaryBudget.substring(1));
                }
                budgetTextView.setText(diaryBudget);
                double spent;
                if(expenseViewModel.getAmountSpentLiveData().getValue() != null) {
                    spent = expenseViewModel.getAmountSpentLiveData().getValue();
                } else {
                    spent = 0;
                }
                updateProgressIndicator(spent, budget);
                if(totExpenseTextView.getVisibility() == View.VISIBLE) {
                    expenseViewModel.filterExpenses(inputFilterCategory);
                }
                updateCurrencyIcon();
            }
        });

        saveBudgetButton.setOnClickListener(saveBudget -> {
            inputBudget = numberEditText.getText().toString().trim();
            String viewModelBudget = homeViewModel.getBudget(SharedPreferencesUtils.getDiaryId(getContext()));
            if(viewModelBudget != null){
                String completeBudget = expenseViewModel.generateTextAmount(String.valueOf(budget), inputCurrency);
                inputCurrency = expenseViewModel.getInputCurrency(completeBudget);
            } else {
                inputCurrency = currencyAutoCompleteTextView.getText().toString().trim();
            }
            boolean correct = expenseViewModel.validateInputBudget(inputBudget, inputCurrency);
            if (correct) {
                budget = Integer.parseInt(inputBudget);
                String completeBudget = expenseViewModel.generateTextAmount(inputBudget, inputCurrency);
                homeViewModel.updateDiaryBudget(SharedPreferencesUtils.getDiaryId(getContext()), completeBudget);
                expenseViewModel.setBudgetOverlayVisibility(false);
            }
        });

        expenseViewModel.getBudgetOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if (visible) {
                showOverlay(BUDGET);
            } else {
                hideOverlay(BUDGET, view);
            }
        });

        expenseViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if(errorMessage != null){
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        //gestione di una spesa
        overlay_add_edit_expense = inflater.inflate(R.layout.overlay_add_edit_expense, expenseRootLayout, false);
        expenseRootLayout.addView(overlay_add_edit_expense);
        overlay_add_edit_expense.setVisibility(View.GONE);
        addExpenseButton = view.findViewById(R.id.addButtonSpese);
        editExpenseButton = view.findViewById(R.id.modificaSpesa);
        deleteExpenseButton = view.findViewById(R.id.eliminaSpesa);

        addExpenseButton.setOnClickListener(addExpenseButtonListener -> {
            bAdd = true;
            expenseViewModel.setExpenseOverlayVisibility(true);
        });

        ImageButton expenseBackButton = view.findViewById(R.id.backSpesaButton);

        expenseBackButton.setOnClickListener(expenseBackButtonListener ->
            expenseViewModel.setExpenseOverlayVisibility(false));

        Button saveExpenseButton = view.findViewById(R.id.salvaSpesa);
        amountEditText = view.findViewById(R.id.inputQuantitaSpesa);
        categoryAutoCompleteTextView = view.findViewById(R.id.inputCategory);
        descriptionEditText = view.findViewById(R.id.inputDescription);
        dayEditText = view.findViewById(R.id.inputDay);
        monthEditText = view.findViewById(R.id.inputMonth);
        yearEditText = view.findViewById(R.id.inputYear);

        expenseViewModel.getExpenseOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if (visible) {
                if(bAdd) {
                    showOverlay(ADD_EXPENSE);
                } else if(bEdit){
                    showOverlay(EDIT_EXPENSE);
                }
            } else {
                if(bAdd){
                    hideOverlay(ADD_EXPENSE, view);
                } else if(bEdit){
                    hideOverlay(EDIT_EXPENSE, view);
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

        saveExpenseButton.setOnClickListener(saveExpenseButtonListener -> {
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

        expenseViewModel.getExpenseEvent().observe(getViewLifecycleOwner(), message -> {
            if(message != null){
                switch (message) {
                    case ADDED:
                        Toast.makeText(requireActivity(), R.string.snackbarExpenseAdded,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATED:
                        Toast.makeText(requireActivity(), R.string.snackbarExpenseUpdated,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DELETED:
                        Toast.makeText(requireActivity(), R.string.snackbarExpenseDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_DELETE:
                        Toast.makeText(requireActivity(), R.string.snackbarExpenseNotDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        overlay_delete = inflater.inflate(R.layout.overlay_delete, expenseRootLayout, false);
        expenseRootLayout.addView(overlay_delete);
        overlay_delete.setVisibility(View.GONE);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button cancelButton = view.findViewById(R.id.cancelDeleteButton);
        TextView deleteText = view.findViewById(R.id.deleteText);
        TextView deleteDescriptionText = view.findViewById(R.id.deleteDescriptionText);
        deleteText.setText(R.string.delete_expense);
        deleteDescriptionText.setText(R.string.delete_expense_description);

        expenseViewModel.getDeleteOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                showOverlay(DELETE_EXPENSE);
            } else {
                hideOverlay(DELETE_EXPENSE, view);
            }
        });

        deleteButton.setOnClickListener(deleteButtonListener -> {
            expenseViewModel.deleteSelectedExpenses();
            expenseViewModel.setDeleteOverlayVisibility(false);
        });

        cancelButton.setOnClickListener(cancelButtonListener -> {
            expenseViewModel.setDeleteOverlayVisibility(false);
            addExpenseButton.setEnabled(false);
        });

        deleteExpenseButton.setOnClickListener(deleteExpenseButtonListener ->
                expenseViewModel.setDeleteOverlayVisibility(true));

        editExpenseButton.setOnClickListener(editExpenseButtonListener -> {
            bEdit = true;
            expenseViewModel.setExpenseOverlayVisibility(true);
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

        filterBackButton.setOnClickListener(filterBackButtonListener -> {
            expenseViewModel.setFilterOverlayVisibility(false);
            bFilter = false;
        });

        filterButton.setOnClickListener(filterButtonListener -> {
            expenseViewModel.setFilterOverlayVisibility(true);
            bFilter = true;
        });

        expenseViewModel.getFilterOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                showOverlay(FILTER);
            } else {
                hideOverlay(FILTER, view);
            }
        });

        expenseViewModel.getFilteredExpensesLiveData().observe(getViewLifecycleOwner(), filteredExpenses -> {
            if (filteredExpenses != null) {
                expensesRecyclerAdapter.setExpenseList(filteredExpenses);
                String amountFilteredExpenses = String.valueOf(expenseViewModel.countAmount(filteredExpenses));
                String totalAmountText = expenseViewModel.generateTextAmount(amountFilteredExpenses, inputCurrency);
                totExpenseTextView.setText(totalAmountText);
            }
        });

        saveCategoryButton.setOnClickListener(saveCategoryButtonListener -> {
            inputFilterCategory = filterCategoryEditText.getText().toString().trim();
            expenseViewModel.filterExpenses(inputFilterCategory);
            expenseViewModel.setFilterOverlayVisibility(false);
            closeFilterButton.setVisibility(View.VISIBLE);
            filterTextView.setVisibility(View.VISIBLE);
            totExpenseTextView.setVisibility(View.VISIBLE);
            addExpenseButton.setEnabled(false);
        });

        closeFilterButton.setOnClickListener(closeFilterButtonListener -> {
            expensesRecyclerAdapter.setExpenseList(expenseViewModel.getAllExpenses());
            closeFilterButton.setVisibility(View.GONE);
            filterTextView.setVisibility(View.GONE);
            totExpenseTextView.setVisibility(View.GONE);
            addExpenseButton.setEnabled(true);
            bFilter = false;
        });

        // gestione modifica progress indicator
        expenseViewModel.getAmountSpentLiveData().observe(getViewLifecycleOwner(), spent ->
                updateProgressIndicator(spent, budget));
    }

    public void updateProgressIndicator(double spent, int budget){
        int progressPercentage = (int) ((spent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor
                    (ContextCompat.getColor(requireContext(), R.color.error));
        else
            progressIndicator.setIndicatorColor
                    (ContextCompat.getColor(requireContext(), R.color.secondary));
        progressIndicator.setProgress(progressPercentage);
        String text = getString(R.string.progressText);
        String formattedText = spent + " / " + budget + " " + text + " (" +
                progressPercentage + "%)";
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
            case DELETE_EXPENSE:
                overlay_delete.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void disableSwipeAndButtons() {
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
        addExpenseButton.setEnabled(false);
        editBudgetButton.setEnabled(false);
        editExpenseButton.setEnabled(false);
        deleteExpenseButton.setEnabled(false);
        filterButton.setEnabled(false);
    }

    private void enableSwipeAndButtons(View view) {
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
        Constants.hideKeyboard(view, requireActivity());
        if(!bFilter) {
            addExpenseButton.setEnabled(true);
        }
        editBudgetButton.setEnabled(true);
        editExpenseButton.setEnabled(true);
        deleteExpenseButton.setEnabled(true);
        filterButton.setEnabled(true);
    }

    private void hideOverlay(String overlayType, View view) {
        enableSwipeAndButtons(view);
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
            case DELETE_EXPENSE:
                overlay_delete.setVisibility(View.GONE);
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
}
