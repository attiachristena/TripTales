package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.CATEGORIES;
import static com.unimib.triptales.util.Constants.CURRENCIES;
import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.CURRENCY_GBP;
import static com.unimib.triptales.util.Constants.CURRENCY_JPY;
import static com.unimib.triptales.util.Constants.CURRENCY_USD;
import static com.unimib.triptales.util.Constants.countAmount;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ExpensesRecyclerAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.ExpenseDao;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.repository.expense.ExpenseRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.source.expense.BaseExpenseLocalDataSource;
import com.unimib.triptales.source.expense.BaseExpenseRemoteDataSource;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModelFactory;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;


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
    private String inputAmountSpent;
    private String inputCategory;
    private String inputDescription;
    private Integer inputDay;
    private Integer inputMonth;
    private Integer inputYear;
    private EditText editTextAmountSpent;
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
    private String inputModifiedAmountSpent;
    private String inputModifiedCategory;
    private String inputModifiedDescription;
    private Integer inputModifiedDay;
    private Integer inputModifiedMonth;
    private Integer inputModifiedYear;
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
    private String inputCategoryFilter;
    private String inputCurrency;
    private AutoCompleteTextView editTextCurrency;
    private TextView noExpensesString;
    private List<Expense> expenseList;
    private List<Expense> filteredExpensesList;
    private ExpenseViewModel expenseViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        IExpenseRepository expenseRepository = ServiceLocator.getINSTANCE().getExpenseRepository(getContext());
        expenseViewModel = new ViewModelProvider(requireActivity(),
                new ExpenseViewModelFactory(expenseRepository)).get(ExpenseViewModel.class);

        expenseList = expenseViewModel.getAllExpenses();

        return inflater.inflate(R.layout.fragment_spese, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        rootLayout = view.findViewById(R.id.rootLayoutSpese);
        noExpensesString = view.findViewById(R.id.noSpeseString);

        if(expenseList.isEmpty()){
            noExpensesString.setVisibility(View.VISIBLE);
        } else {
            noExpensesString.setVisibility(View.GONE);
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
        }

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
                if (inputBudget.isEmpty()) {
                    numberEditText.setError("Inserisci una quantità");
                } else if (inputCurrency.isEmpty()) {
                    editTextCurrency.setError("Scegli una valuta");
                } else if (inputBudget.length() > 9) {
                    numberEditText.setError("Inserisci un numero più basso");
                } else {
                    numberEditText.setError(null);
                    budget = Integer.parseInt(inputBudget);
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
                        tmp = budget+inputCurrency;
                    else
                        tmp = inputCurrency+budget;
                    budgetText.setText(tmp);

                    updateProgressIndicator(spent, budget, 0);
                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_budget.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addExpense.setVisibility(View.VISIBLE);
                    filterButton.setEnabled(true);
                    if(totExpense.getVisibility() == View.VISIBLE) {
                        filteredExpensesList = expenseViewModel.getFilteredExpenses(inputCategoryFilter);
                        String tmp2 = countAmount(filteredExpensesList, inputCurrency);
                        totExpense.setText(tmp2);
                    }
                    updateCurrencyIcon();
                }
            }
        });

        addExpense = view.findViewById(R.id.addButtonSpese);
        overlay_add_expense = inflater.inflate(R.layout.overlay_add_expense, rootLayout, false);
        rootLayout.addView(overlay_add_expense);
        overlay_add_expense.setVisibility(View.GONE);
        modifyExpense = view.findViewById(R.id.modificaSpesa);
        deleteExpense = view.findViewById(R.id.eliminaSpesa);
        RecyclerView recyclerViewExpenses = view.findViewById(R.id.recyclerViewExpenses);
        ExpensesRecyclerAdapter recyclerAdapter = new ExpensesRecyclerAdapter(expenseList,  getContext(),
                addExpense, modifyExpense, deleteExpense);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewExpenses.setAdapter(recyclerAdapter);

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
                    editTextAmountSpent.setText("");
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
        editTextAmountSpent = view.findViewById(R.id.inputQuantitaSpesa);
        editTextCategory = view.findViewById(R.id.inputCategory);
        editTextDescription = view.findViewById(R.id.inputDescription);
        editTextDay = view.findViewById(R.id.inputDay);
        editTextMonth = view.findViewById(R.id.inputMonth);
        editTextYear = view.findViewById(R.id.inputYear);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        editTextCategory.setAdapter(adapterCategory);

        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense currentExpense;
                inputCategory = editTextCategory.getText().toString().trim();;
                inputDescription = editTextDescription.getText().toString().trim();

                if (editTextDay.getText().toString().isEmpty()) {
                    editTextDay.setError("Inserisci un giorno");
                } else {
                    inputDay = Integer.parseInt(editTextDay.getText().toString());
                    if (inputDay < 1 || inputDay > 31) {
                        editTextDay.setError("Inserisci un giorno valido");
                    } else {
                        editTextDay.setError(null);
                    }
                }

                if (editTextMonth.getText().toString().isEmpty()) {
                    editTextMonth.setError("Inserisci un mese");
                } else {
                    inputMonth = Integer.parseInt(editTextMonth.getText().toString());
                    if (inputMonth < 1 || inputMonth > 12) {
                        editTextMonth.setError("Inserisci un mese valido");
                    } else {
                        editTextMonth.setError(null);
                    }
                }

                if (editTextYear.getText().toString().isEmpty()) {
                    editTextYear.setError("Inserisci un anno");
                } else {
                    inputYear = Integer.parseInt(editTextYear.getText().toString());
                    if (inputYear < 2000 || inputYear > 2100) {
                        editTextYear.setError("Inserisci un anno valido");
                    } else {
                        editTextYear.setError(null);
                    }
                }

                inputAmountSpent = editTextAmountSpent.getText().toString().trim();

                if (inputAmountSpent.isEmpty()) {
                    editTextAmountSpent.setError("Inserisci una quantità");
                } else if (inputCategory.isEmpty()) {
                    editTextCategory.setError("Scegli una categoria");
                } else if (inputDescription.isEmpty()) {
                    editTextDescription.setError("Inserisci una descrizione");
                } else if (editTextDay.getError() != null){
                    editTextDay.setError("Inserisci un giorno");
                } else if (editTextMonth.getError() != null){
                    editTextMonth.setError("Inserisci un mese");
                } else if (editTextYear.getError() != null){
                    editTextYear.setError("Inserisci un anno");
                } else if (inputMonth < 1 || inputMonth > 12) {
                    editTextMonth.setError("Inserisci un mese valido");
                } else if (inputYear < 2000 || inputYear > 2100) {
                    editTextYear.setError("Inserisci un anno valido");
                } else {
                    editTextCategory.setError(null);
                    editTextDescription.setError(null);
                    editTextDay.setError(null);
                    editTextMonth.setError(null);
                    editTextYear.setError(null);
                    String dataCompleta = getString(R.string.dataCompleta, inputDay, inputMonth, inputYear);
                    editTextAmountSpent.setError(null);

                    double spesa = Double.parseDouble(inputAmountSpent);
                    updateProgressIndicator(spesa, budget, 1);
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
                        tmp = spesa+inputCurrency;
                    else
                        tmp = inputCurrency+spesa;

                    currentExpense = new Expense(tmp, inputCategory, inputDescription,
                            dataCompleta, false);
                    long id = expenseViewModel.insertExpense(currentExpense);
                    currentExpense.setId((int) id);
                    expenseList.add(currentExpense);
                    recyclerAdapter.notifyItemInserted(expenseList.size()-1);

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_expense.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addExpense.setVisibility(View.VISIBLE);
                    editBudget.setEnabled(true);
                    filterButton.setEnabled(true);
                    noExpensesString.setVisibility(View.GONE);
                }
            }
        });

        deleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountText;
                double totExpense = 0;
                int index = 0;
                Iterator<Expense> iterator = expenseList.iterator();
                while (iterator.hasNext()) {
                    Expense e = iterator.next();
                    if (e.isExpense_isSelected()) {
                        amountText = e.getAmount();
                        String amountSubstring;
                        if (inputCurrency.equalsIgnoreCase(CURRENCY_EUR)){
                            amountSubstring = amountText.substring(0, amountText.length() - 1);
                        } else {
                            amountSubstring = amountText.substring(1);
                        }
                        totExpense += Double.parseDouble(amountSubstring);
                        iterator.remove();
                        recyclerAdapter.notifyItemRemoved(index);
                        expenseViewModel.deleteExpense(e);
                    } else {
                        index++;
                    }
                }

                spent = spent - totExpense;
                updateProgressIndicator(spent, budget, 0);

                modifyExpense.setVisibility(View.GONE);
                deleteExpense.setVisibility(View.GONE);
                addExpense.setEnabled(true);
                if(expenseList.isEmpty()){
                    noExpensesString.setVisibility(View.VISIBLE);
                }
            }
        });


        overlay_modify_expense = inflater.inflate(R.layout.overlay_modify_expense, rootLayout, false);
        rootLayout.addView(overlay_modify_expense);
        overlay_modify_expense.setVisibility(View.GONE);

        modifyExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_expense.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);

                List<Expense> selectedExpenses = expenseViewModel.getSelectedExpenses();
                Expense currentExpense = selectedExpenses.get(0);

                String amount = currentExpense.getAmount();
                String tmp;
                if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
                    tmp = amount.substring(0, amount.length()-1);
                else
                    tmp = amount.substring(1);
                editTextModifiedAmountSpent.setText(tmp);

                editTextModifiedCategory.setText(currentExpense.getCategory(), false);
                editTextModifiedDescription.setText(currentExpense.getDescription());

                String date = currentExpense.getDate();
                if(date.charAt(1) == '/'){
                    inputModifiedDay = (int)(date.charAt(0)) - '0';
                    date = date.substring(2);
                }else{
                    inputModifiedDay = Integer.parseInt(date.substring(0, 2));
                    date = date.substring(3);
                }
                editTextModifiedDay.setText(String.valueOf(inputModifiedDay));

                if(date.charAt(1) == '/'){
                    inputModifiedMonth =(int)(date.charAt(0)) - '0';
                    date = date.substring(2);
                }else{
                    inputModifiedMonth = Integer.parseInt(date.substring(0, 2));
                    date = date.substring(3);
                }
                editTextModifiedMonth.setText(String.valueOf(inputModifiedMonth));

                inputModifiedYear = Integer.parseInt(date);
                editTextModifiedYear.setText(String.valueOf(inputModifiedYear));

                addExpense.setVisibility(View.GONE);
                modifyExpense.setVisibility(View.GONE);
                deleteExpense.setVisibility(View.GONE);
                editBudget.setEnabled(false);
                filterButton.setEnabled(false);
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

        saveModifiedExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Expense> selectedExpenses = expenseViewModel.getSelectedExpenses();
                Expense currentExpense = selectedExpenses.get(0);

                String amountText = currentExpense.getAmount();
                String amountSubstring;
                if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
                    amountSubstring = amountText.substring(0, amountText.length()-1);
                else
                    amountSubstring = amountText.substring(1);
                double amount = Double.parseDouble(amountSubstring);
                spent = spent - amount;

                inputModifiedCategory = editTextModifiedCategory.getText().toString().trim();

                inputModifiedDescription = editTextModifiedDescription.getText().toString().trim();

                inputModifiedDay = Integer.parseInt(editTextModifiedDay.getText().toString());
                inputModifiedMonth = Integer.parseInt(editTextModifiedMonth.getText().toString());
                inputModifiedYear = Integer.parseInt(editTextModifiedYear.getText().toString());

                inputModifiedAmountSpent = editTextModifiedAmountSpent.getText().toString().trim();

                if (inputModifiedAmountSpent.isEmpty()) {
                    editTextModifiedAmountSpent.setError("Inserisci una data");
                } else if (inputModifiedCategory.isEmpty()) {
                    editTextModifiedCategory.setError("Scegli una categoria");
                } else if (inputModifiedDescription.isEmpty()) {
                    editTextModifiedDescription.setError("Inserisci una descrizione");
                } else if (inputModifiedDay == null) {
                    editTextModifiedDay.setError("Inserisci un giorno");
                } else if (inputModifiedDay < 1 || inputModifiedDay > 31) {
                    editTextModifiedDay.setError("Inserisci un giorno valido");
                } else if (inputModifiedMonth == null) {
                    editTextModifiedMonth.setError("Inserisci un mese");
                } else if (inputModifiedMonth < 1 || inputModifiedMonth > 12) {
                    editTextModifiedMonth.setError("Inserisci un mese valido");
                } else if (inputModifiedYear == null) {
                    editTextModifiedYear.setError("Inserisci un anno");
                } else if (inputModifiedYear < 2000 || inputModifiedYear > 2100) {
                    editTextModifiedYear.setError("Inserisci un anno valido");
                } else {
                    editTextModifiedCategory.setError(null);
                    currentExpense.setCategory(inputModifiedCategory);
                    expenseViewModel.updateExpenseCategory(currentExpense.getId(), inputModifiedCategory);
                    editTextModifiedDescription.setError(null);
                    currentExpense.setDescription(inputModifiedDescription);
                    expenseViewModel.updateExpenseDescription(currentExpense.getId(), inputModifiedDescription);
                    editTextModifiedDay.setError(null);
                    editTextModifiedMonth.setError(null);
                    editTextModifiedYear.setError(null);
                    String dataCompleta = getString(R.string.dataCompleta, inputModifiedDay, inputModifiedMonth, inputModifiedYear);
                    currentExpense.setDate(dataCompleta);
                    expenseViewModel.updateExpenseDate(currentExpense.getId(), dataCompleta);
                    editTextModifiedAmountSpent.setError(null);

                    double spesa = Double.parseDouble(inputModifiedAmountSpent);
                    updateProgressIndicator(spesa, budget, 1);
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
                        tmp = spesa+inputCurrency;
                    else
                        tmp = inputCurrency+spesa;
                    currentExpense.setAmount(tmp);
                    expenseViewModel.updateExpenseAmount(currentExpense.getId(), tmp);

                    currentExpense.setExpense_isSelected(false);
                    expenseViewModel.updateExpenseIsSelected(currentExpense.getId(), false);

                    int position = expenseList.indexOf(currentExpense);
                    if (position != -1) {
                        expenseList.set(position, currentExpense); // Aggiorna l'oggetto nella lista
                        recyclerAdapter.notifyItemChanged(position);
                    }

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_modify_expense.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addExpense.setVisibility(View.VISIBLE);
                    addExpense.setEnabled(true);
                    editBudget.setEnabled(true);
                    filterButton.setEnabled(true);
                }
            }
        });

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

        saveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCategoryFilter = editTextCategoryFilter.getText().toString().trim();
                if (inputCategoryFilter.isEmpty()) {
                    editTextCategoryFilter.setError("Scegli una categoria");
                } else {
                    List<Expense> filteredExpenses =
                            expenseViewModel.getFilteredExpenses(inputCategoryFilter);

                    expenseList.clear();
                    expenseList.addAll(filteredExpenses);
                    recyclerAdapter.notifyDataSetChanged();

                    String tmp = countAmount(filteredExpenses, inputCurrency);
                    totExpense.setText(tmp);
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
            }
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Expense> allExpenses = expenseViewModel.getAllExpenses();
                expenseList.clear();
                expenseList.addAll(allExpenses);
                recyclerAdapter.notifyDataSetChanged();
                closeFilter.setVisibility(View.GONE);
                filterText.setVisibility(View.GONE);
                totExpense.setVisibility(View.GONE);
                addExpense.setEnabled(true);
            }
        });
    }

    public void updateProgressIndicator(double expense, int budget, int add){
        if(add == 1)
            spent = spent + expense;
        int progressPercentage = (int) ((spent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.error));
        else
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.secondary));
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
