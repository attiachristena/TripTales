package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.CATEGORIES;
import static com.unimib.triptales.util.Constants.CURRENCIES;
import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.CURRENCY_GBP;
import static com.unimib.triptales.util.Constants.CURRENCY_JPY;
import static com.unimib.triptales.util.Constants.CURRENCY_USD;

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
    private double spent;
    private TextView progressText;
    private String inputBudget;
    private ImageButton editBudget;
    private LinearProgressIndicator progressIndicator;
    private TextView budgetText;
    private EditText numberEditText;
    private FloatingActionButton addExpense;
    private EditText editTextAmount;
    private AutoCompleteTextView editTextCategory;
    private EditText editTextDescription;
    private EditText editTextDay;
    private EditText editTextMonth;
    private EditText editTextYear;
    private ConstraintLayout rootLayout;
    private FloatingActionButton modifyExpense;
    private FloatingActionButton deleteExpense;
    private View overlay_add_expense;
    private View overlay_add_budget;
    private EditText editTextModifiedAmountSpent;
    private AutoCompleteTextView editTextModifiedCategory;
    private EditText editTextModifiedDescription;
    private EditText editTextModifiedDay;
    private EditText editTextModifiedMonth;
    private EditText editTextModifiedYear;
    private View overlay_modify_expense;
    private Button filterButton;
    private ImageButton closeFilter;
    private TextView totExpense;
    private TextView filterText;
    private View overlay_filter;
    private AutoCompleteTextView editTextCategoryFilter;
    private String inputCurrency;
    private AutoCompleteTextView editTextCurrency;
    private TextView noExpensesString;
    private List<Expense> expenseList;
    private ExpenseViewModel expenseViewModel;
    private ExpensesRecyclerAdapter recyclerAdapter;
    private String inputCategoryFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spese, container, false);
        IExpenseRepository expenseRepository = ServiceLocator.getINSTANCE().getExpenseRepository(getContext());
        expenseViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(expenseRepository)).get(ExpenseViewModel.class);

        expenseList = expenseViewModel.getAllExpenses();
        expenseViewModel.deselectAllExpenses();

        RecyclerView recyclerViewExpenses = rootView.findViewById(R.id.recyclerViewExpenses);
        recyclerAdapter = new ExpensesRecyclerAdapter(expenseList, getContext());
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewExpenses.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnExpenseClickListener((expense, card) -> {
            expenseViewModel.toggleExpenseSelection(expense);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        rootLayout = view.findViewById(R.id.rootLayoutSpese);
        noExpensesString = view.findViewById(R.id.noSpeseString);

        /*if(expenseList.isEmpty()){
            noExpensesString.setVisibility(View.VISIBLE);
        } else {
            noExpensesString.setVisibility(View.GONE);*/
            /*  da inserire quando il budget viene aggiunto al database
            double totExpense = 0;
            for(Expense e: expenseList){
                String amountText = e.getAmount();
                String amountSubstring;
                if(inputCurrency.equalsIgnoreCase("€"))
                    amountSubstring = amountText.substring(0, amountText.length()-1);
                else
                    amountSubstring = amountText.substring(1);
                totExpense += Double.parseDouble(amountSubstring);
                updateProgressIndicator(totExpense, budget, 1);
            }
             */
        //}

        // gestione del budget
        overlay_add_budget = inflater.inflate(R.layout.overlay_add_budget, rootLayout, false);
        rootLayout.addView(overlay_add_budget);
        overlay_add_budget.setVisibility(View.GONE);

        Button saveBudget = view.findViewById(R.id.salvaBudget);
        editBudget = view.findViewById(R.id.editBudget);
        ImageButton backButtonBudget = view.findViewById(R.id.backButtonBudget);

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_budget.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
                addExpense.setVisibility(View.GONE);
                filterButton.setEnabled(false);
                /*if(expenseList.isEmpty()){
                    textFieldCurrency.setEnabled(true);
                } else {
                    textFieldCurrency.setEnabled(false);
                    textFieldCurrency.setBoxBackgroundColor(getResources().getColor(R.color.background_overlays));
                }*/
            }
        });

        backButtonBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_budget.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addExpense.setVisibility(View.VISIBLE);
                filterButton.setEnabled(true);
            }
        });

        progressText = view.findViewById(R.id.progressText);
        numberEditText = view.findViewById(R.id.inputBudget);
        editTextCurrency = view.findViewById(R.id.inputCurrency);
        budgetText = view.findViewById(R.id.totBudget);
        progressIndicator = view.findViewById(R.id.budgetProgressIndicator);
        totExpense = view.findViewById(R.id.totSpesa);

        ArrayAdapter<String> adapterBudget = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CURRENCIES);
        editTextCurrency.setAdapter(adapterBudget);

        saveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputBudget = numberEditText.getText().toString().trim();
                inputCurrency = editTextCurrency.getText().toString().trim();

                boolean correct = expenseViewModel.validateInputBudget(inputBudget, inputCurrency);
                if (correct) {
                    budget = Integer.parseInt(inputBudget);
                    budgetText.setText(expenseViewModel.generateTextAmount(inputBudget, inputCurrency));
                    // aggiungere un MutableLiveData<int> budgetLiveData a DiaryViewModel e
                    // poi gestire .observe aggiornando il progress indicator
                    updateProgressIndicator(spent, budget, 0);
                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_budget.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addExpense.setVisibility(View.VISIBLE);
                    filterButton.setEnabled(true);
                    if(totExpense.getVisibility() == View.VISIBLE) {
                        expenseViewModel.filterExpenses(inputCategoryFilter, inputCurrency);
                    }
                    updateCurrencyIcon();
                }
            }
        });

        //gestione aggiunta di una spesa
        overlay_add_expense = inflater.inflate(R.layout.overlay_add_expense, rootLayout, false);
        rootLayout.addView(overlay_add_expense);
        overlay_add_expense.setVisibility(View.GONE);
        addExpense = view.findViewById(R.id.addButtonSpese);
        modifyExpense = view.findViewById(R.id.modificaSpesa);
        deleteExpense = view.findViewById(R.id.eliminaSpesa);

        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            recyclerAdapter.setExpenseList(expenses);
            if(expenses.isEmpty()){
                noExpensesString.setVisibility(View.VISIBLE);
            } else {
                noExpensesString.setVisibility(View.GONE);
            }
        });

        expenseViewModel.getSelectedExpensesLiveData().observe(getViewLifecycleOwner(), selectedExpenses -> {
            if(selectedExpenses != null) {
                if (selectedExpenses.size() == 1) {
                    if(overlay_modify_expense.getVisibility() == View.VISIBLE){
                        modifyExpense.setVisibility(View.GONE);
                        deleteExpense.setVisibility(View.GONE);
                    } else {
                        addExpense.setEnabled(false);
                        modifyExpense.setVisibility(View.VISIBLE);
                        deleteExpense.setVisibility(View.VISIBLE);
                    }
                } else if (selectedExpenses.size() == 2) {
                    addExpense.setEnabled(false);
                    modifyExpense.setVisibility(View.GONE);
                } else if (selectedExpenses.isEmpty()) {
                    modifyExpense.setVisibility(View.GONE);
                    deleteExpense.setVisibility(View.GONE);
                    addExpense.setEnabled(true);
                }
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(budget == 0){
                    Snackbar snackbar = Snackbar.make(rootLayout, R.string.snackbarErroreBudget, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else {
                    overlay_add_expense.setVisibility(View.VISIBLE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
                    addExpense.setVisibility(View.GONE);
                    editBudget.setEnabled(false);
                    filterButton.setEnabled(false);
                    editTextAmount.setText("");
                    editTextCategory.setText("");
                    editTextDescription.setText("");
                    editTextDay.setText("");
                    editTextMonth.setText("");
                    editTextYear.setText("");
                }
            }
        });

        ImageButton backButtonExpense = view.findViewById(R.id.backSpesaButton);

        backButtonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_expense.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addExpense.setVisibility(View.VISIBLE);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        Button saveExpense = view.findViewById(R.id.salvaSpesa);
        editTextAmount = view.findViewById(R.id.inputQuantitaSpesa);
        editTextCategory = view.findViewById(R.id.inputCategory);
        editTextDescription = view.findViewById(R.id.inputDescription);
        editTextDay = view.findViewById(R.id.inputDay);
        editTextMonth = view.findViewById(R.id.inputMonth);
        editTextYear = view.findViewById(R.id.inputYear);

        editTextDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
                    editTextMonth.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
                    editTextYear.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        editTextCategory.setAdapter(adapterCategory);

        expenseViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if(errorMessage != null){
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "Tutto corretto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputAmount = editTextAmount.getText().toString().trim();
                String inputCategory = editTextCategory.getText().toString().trim();;
                String inputDescription = editTextDescription.getText().toString().trim();
                String inputDay = editTextDay.getText().toString().trim();
                String inputMonth = editTextMonth.getText().toString().trim();
                String inputYear = editTextYear.getText().toString().trim();

                boolean correct = expenseViewModel.validateInputExpense(inputAmount, inputCategory,
                        inputDescription, inputDay, inputMonth, inputYear);

                if(correct) {
                    expenseViewModel.insertExpense(inputAmount, inputCategory,
                            inputDescription, inputDay, inputMonth, inputYear,
                            inputCurrency).observe(getViewLifecycleOwner(), insertedExpense -> {
                        Constants.hideKeyboard(view, requireActivity());
                        overlay_add_expense.setVisibility(View.GONE);
                        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                        addExpense.setVisibility(View.VISIBLE);
                        editBudget.setEnabled(true);
                        filterButton.setEnabled(true);
                    });
                }
            }
        });

        // gestione rimozione della spesa
        deleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Expense> selectedExpenses = expenseViewModel.getSelectedExpensesLiveData().getValue();
                if (selectedExpenses != null && !selectedExpenses.isEmpty()) {
                    expenseViewModel.deleteSelectedExpenses(selectedExpenses, inputCurrency);
                }
            }
        });

        // overlay_add_or_edit_expense
        // gestione modifica della spesa
        overlay_modify_expense = inflater.inflate(R.layout.overlay_modify_expense, rootLayout, false);
        rootLayout.addView(overlay_modify_expense);
        overlay_modify_expense.setVisibility(View.GONE);

        modifyExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_expense.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);

                List<Expense> selectedExpenses = expenseViewModel.getSelectedExpensesLiveData().getValue();

                if (selectedExpenses != null && !selectedExpenses.isEmpty()) {
                    Expense currentExpense = selectedExpenses.get(0);
                    String tmp = expenseViewModel.extractRealAmount(currentExpense, inputCurrency);
                    editTextModifiedAmountSpent.setText(tmp);
                    editTextModifiedCategory.setText(currentExpense.getCategory(), false);
                    editTextModifiedDescription.setText(currentExpense.getDescription());

                    int[] extractedDate = expenseViewModel.extractDayMonthYear(currentExpense.getDate());
                    editTextModifiedDay.setText(String.valueOf(extractedDate[0]));
                    editTextModifiedMonth.setText(String.valueOf(extractedDate[1]));
                    editTextModifiedYear.setText(String.valueOf(extractedDate[2]));

                    addExpense.setVisibility(View.GONE);
                    modifyExpense.setVisibility(View.GONE);
                    deleteExpense.setVisibility(View.GONE);
                    editBudget.setEnabled(false);
                    filterButton.setEnabled(false);
                }
            }
        });

        ImageButton backButtonModifyExpense = view.findViewById(R.id.backModificaSpesaButton);

        backButtonModifyExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_expense.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addExpense.setVisibility(View.VISIBLE);
                modifyExpense.setVisibility(View.VISIBLE);
                deleteExpense.setVisibility(View.VISIBLE);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        Button saveModifiedExpense = view.findViewById(R.id.salvaModificaSpesa);
        editTextModifiedAmountSpent = view.findViewById(R.id.inputModificaQuantitaSpesa);
        editTextModifiedCategory = view.findViewById(R.id.inputModificaCategory);
        editTextModifiedDescription = view.findViewById(R.id.inputModificaDescription);
        editTextModifiedDay = view.findViewById(R.id.inputModifyDay);
        editTextModifiedMonth = view.findViewById(R.id.inputModifyMonth);
        editTextModifiedYear = view.findViewById(R.id.inputModifyYear);
        editTextModifiedCategory.setAdapter(adapterCategory);

        editTextModifiedDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
                    editTextModifiedMonth.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextModifiedMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
                    editTextModifiedYear.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        saveModifiedExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Expense> selectedExpenses = expenseViewModel.getSelectedExpensesLiveData().getValue();

                if (selectedExpenses != null && !selectedExpenses.isEmpty()) {
                    Expense currentExpense = selectedExpenses.get(0);

                    String inputAmount = editTextModifiedAmountSpent.getText().toString().trim();
                    String inputCategory = editTextModifiedCategory.getText().toString().trim();
                    String inputDescription = editTextModifiedDescription.getText().toString().trim();
                    String inputDay = editTextModifiedDay.getText().toString();
                    String inputMonth = editTextModifiedMonth.getText().toString();
                    String inputYear = editTextModifiedYear.getText().toString();

                    boolean correct = expenseViewModel.validateInputExpense(inputAmount,
                            inputCategory, inputDescription, inputDay, inputMonth, inputYear);

                    if (correct) {
                        expenseViewModel.updateExpense(currentExpense, inputCurrency, inputAmount,
                                inputCategory, inputDescription, inputDay, inputMonth, inputYear);

                        Constants.hideKeyboard(view, requireActivity());
                        overlay_modify_expense.setVisibility(View.GONE);
                        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                        addExpense.setVisibility(View.VISIBLE);
                        addExpense.setEnabled(true);
                        editBudget.setEnabled(true);
                        filterButton.setEnabled(true);
                        expenseViewModel.deselectAllExpenses();
                    }
                }
            }
        });

        // gestione del filtro delle spese
        overlay_filter = inflater.inflate(R.layout.overlay_filter, rootLayout, false);
        rootLayout.addView(overlay_filter);
        overlay_filter.setVisibility(View.GONE);

        filterButton = view.findViewById(R.id.buttonFilter);
        ImageButton backButtonFilter = view.findViewById(R.id.backButtonFilter);
        Button saveCategory = view.findViewById(R.id.saveCategory);
        closeFilter = view.findViewById(R.id.closeFilter);
        filterText = view.findViewById(R.id.testoFiltro);
        editTextCategoryFilter = view.findViewById(R.id.inputCategoryFilter);
        editTextCategoryFilter.setAdapter(adapterCategory);

        backButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_filter.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                addExpense.setVisibility(View.VISIBLE);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_filter.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
                editTextCategoryFilter.setText("", false);
                addExpense.setVisibility(View.GONE);
                editBudget.setEnabled(false);
                filterButton.setEnabled(false);
            }
        });

        expenseViewModel.getFilteredExpensesLiveData().observe(getViewLifecycleOwner(), filteredExpenses -> {
            if (filteredExpenses != null) {
                recyclerAdapter.setExpenseList(filteredExpenses);

                String tmp = String.valueOf(expenseViewModel.countAmount(filteredExpenses, inputCurrency));
                String totalAmountText = expenseViewModel.generateTextAmount(tmp, inputCurrency);
                totExpense.setText(totalAmountText);

                overlay_filter.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                closeFilter.setVisibility(View.VISIBLE);
                filterText.setVisibility(View.VISIBLE);
                totExpense.setVisibility(View.VISIBLE);
                addExpense.setVisibility(View.VISIBLE);
                addExpense.setEnabled(false);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        saveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCategoryFilter = editTextCategoryFilter.getText().toString().trim();
                expenseViewModel.filterExpenses(inputCategoryFilter, inputCurrency);
            }
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAdapter.setExpenseList(expenseList);
                closeFilter.setVisibility(View.GONE);
                filterText.setVisibility(View.GONE);
                totExpense.setVisibility(View.GONE);
                addExpense.setEnabled(true);
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
                progressText.setText(formattedText);
            }
        });

    }

    public void updateProgressIndicator(double expense, int budget, int add){
        if(add == 1)
            spent = spent + expense;
        int progressPercentage = (int) ((spent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.error));
        else
            progressIndicator.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.secondary));
        progressIndicator.setProgress(progressPercentage);
        String formattedText = spent + " / " + budget + " spesi" + " (" + progressPercentage + "%)";
        progressText.setText(formattedText);
    }

    public void updateCurrencyIcon(){
        TextInputLayout textQuantity = overlay_add_expense.findViewById(R.id.textFieldQuantita);
        TextInputLayout textModifyQuantity = overlay_modify_expense.findViewById(R.id.textFieldModificaQuantita);
        if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
        }else if(inputCurrency.equalsIgnoreCase(CURRENCY_USD)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
        }else if(inputCurrency.equalsIgnoreCase(CURRENCY_GBP)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
        }else if(inputCurrency.equalsIgnoreCase(CURRENCY_JPY)){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
        }
    }


}
