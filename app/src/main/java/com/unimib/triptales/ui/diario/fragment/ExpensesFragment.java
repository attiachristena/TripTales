package com.unimib.triptales.ui.diario.fragment;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;
import static com.unimib.triptales.util.Constants.CATEGORIES;
import static com.unimib.triptales.util.Constants.countSelectedCards;
import static com.unimib.triptales.util.Constants.findSelectedCard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ExpensesRecyclerAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.ExpenseDao;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class ExpensesFragment extends Fragment {

    int budget;
    double spent;
    Button saveBudget;
    TextView progressText;
    String inputBudget;
    ImageButton editBudget;
    ImageButton backButtonBudget;
    int progressPercentage;
    LinearProgressIndicator progressIndicator;
    TextView budgetText;
    EditText numberEditText;
    String formattedText;
    FloatingActionButton addExpense;
    ImageButton backButtonExpense;
    Button saveExpense;
    String inputAmountSpent;
    String inputCategory;
    String inputDescription;
    Integer inputDay;
    Integer inputMonth;
    Integer inputYear;
    EditText editTextAmountSpent;
    AutoCompleteTextView editTextCategory;
    EditText editTextDescription;
    EditText editTextDay;
    EditText editTextMonth;
    EditText editTextYear;
    ConstraintLayout rootLayout;
    FloatingActionButton modifyExpense;
    FloatingActionButton deleteExpense;
    ArrayList<MaterialCardView> listExpenseCards;
    int index;
    View overlay_add_expense;
    LayoutInflater inflater;
    View overlay_add_budget;
    ImageButton backButtonModifyExpense;
    Button saveModifiedExpense;
    String inputModifiedAmountSpent;
    String inputModifiedCategory;
    String inputModifiedDescription;
    Integer inputModifiedDay;
    Integer inputModifiedMonth;
    Integer inputModifiedYear;
    EditText editTextModifiedAmountSpent;
    AutoCompleteTextView editTextModifiedCategory;
    EditText editTextModifiedDescription;
    EditText editTextModifiedDay;
    EditText editTextModifiedMonth;
    EditText editTextModifiedYear;
    View overlay_modify_expense;
    Button filterButton;
    ImageButton closeFilter;
    TextView totExpense;
    TextView filterText;
    View overlay_filter;
    AutoCompleteTextView editTextCategoryFilter;
    String inputCategoryFilter;
    ImageButton backButtonFilter;
    ArrayList<MaterialCardView> filteredCards;
    Button saveCategory;
    String inputCurrency;
    String inputOldCurrency;
    AutoCompleteTextView editTextCurrency;
    TextInputLayout textFieldCurrency;
    TextView noExpensesString;
    RecyclerView recyclerViewExpenses;
    private List<Expense> expenseList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        expenseList = AppRoomDatabase.getDatabase(getContext()).expenseDao().getAll();
        return inflater.inflate(R.layout.fragment_spese, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflater = LayoutInflater.from(view.getContext());
        listExpenseCards = new ArrayList<>();
        rootLayout = view.findViewById(R.id.rootLayoutSpese);
        noExpensesString = view.findViewById(R.id.noSpeseString);
        if(expenseList.isEmpty()){
            noExpensesString.setVisibility(View.VISIBLE);
        } else {
            noExpensesString.setVisibility(View.GONE);
        }

        // Card per modificare il budget

        overlay_add_budget = inflater.inflate(R.layout.overlay_add_budget, rootLayout, false);
        rootLayout.addView(overlay_add_budget);
        overlay_add_budget.setVisibility(View.GONE);

        saveBudget = view.findViewById(R.id.salvaBudget);
        editBudget = view.findViewById(R.id.editBudget);
        backButtonBudget = view.findViewById(R.id.backButtonBudget);

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_budget.setVisibility(View.VISIBLE);
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
        inputOldCurrency = editTextCurrency.getText().toString().trim();
        textFieldCurrency = view.findViewById(R.id.textFieldCurrency);
        totExpense = view.findViewById(R.id.totSpesa);

        String[] itemsCurrency = {"€", "$", "£", "¥"};
        ArrayAdapter<String> adapterBudget = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, itemsCurrency);
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
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = budget+inputCurrency;
                    else
                        tmp = inputCurrency+budget;
                    budgetText.setText(tmp);
                    updateProgressIndicator(spent, budget, 0);
                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_budget.setVisibility(View.GONE);
                    addExpense.setVisibility(View.VISIBLE);
                    filterButton.setEnabled(true);
                    if(totExpense.getVisibility() == View.VISIBLE) {
                        double countSpese = countAmountByCategory();
                        String tmp2;
                        if (inputCurrency.equalsIgnoreCase("€"))
                            tmp2 = countSpese + inputCurrency;
                        else
                            tmp2 = inputCurrency + countSpese;
                        totExpense.setText(tmp2);
                    }
                    updateCurrencyIcon();
                    inputOldCurrency = inputCurrency;
                }
            }
        });

        //Card per aggiungere una nuova spesa

        addExpense = view.findViewById(R.id.addButtonSpese);
        overlay_add_expense = inflater.inflate(R.layout.overlay_add_spesa, rootLayout, false);
        rootLayout.addView(overlay_add_expense);
        overlay_add_expense.setVisibility(View.GONE);
        modifyExpense = view.findViewById(R.id.modificaSpesa);
        deleteExpense = view.findViewById(R.id.eliminaSpesa);
        recyclerViewExpenses = view.findViewById(R.id.recyclerViewExpenses);
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

        backButtonExpense = view.findViewById(R.id.backSpesaButton);

        backButtonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_expense.setVisibility(View.GONE);
                Constants.hideKeyboard(view, requireActivity());
                addExpense.setVisibility(View.VISIBLE);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        saveExpense = view.findViewById(R.id.salvaSpesa);
        editTextAmountSpent = view.findViewById(R.id.inputQuantitaSpesa);
        editTextCategory = view.findViewById(R.id.inputCategory);
        editTextDescription = view.findViewById(R.id.inputDescription);
        editTextDay = view.findViewById(R.id.inputDay);
        editTextMonth = view.findViewById(R.id.inputMonth);
        editTextYear = view.findViewById(R.id.inputYear);
        //index = 0;

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
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = spesa+inputCurrency;
                    else
                        tmp = inputCurrency+spesa;

                    currentExpense = new Expense(tmp, inputCategory, inputDescription,
                            dataCompleta, false);
                    expenseList.add(currentExpense);
                    recyclerAdapter.notifyItemInserted(expenseList.size()-1);
                    AppRoomDatabase.getDatabase(view.getContext()).expenseDao().insert(currentExpense);

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_expense.setVisibility(View.GONE);
                    addExpense.setVisibility(View.VISIBLE);
                    editBudget.setEnabled(true);
                    filterButton.setEnabled(true);
                    noExpensesString.setVisibility(View.GONE);
                }
            }
        });

        // per eliminare una spesa

        deleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountText;
                double totExpense = 0;
                int index = 0;
                Iterator<Expense> iterator = expenseList.iterator();
                while (iterator.hasNext()) {
                    Expense e = iterator.next();
                    if (e.isSelected) {
                        amountText = e.amount;
                        String amountSubstring;
                        if (inputCurrency.equalsIgnoreCase("€")){
                            amountSubstring = amountText.substring(0, amountText.length() - 1);
                        } else {
                            amountSubstring = amountText.substring(1);
                        }
                        totExpense += Double.parseDouble(amountSubstring);
                        iterator.remove();
                        recyclerAdapter.notifyItemRemoved(index);
                        AppRoomDatabase.getDatabase(getContext()).expenseDao().delete(e);
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

        // per modificare una spesa

        overlay_modify_expense = inflater.inflate(R.layout.overlay_modifica_spesa, rootLayout, false);
        rootLayout.addView(overlay_modify_expense);
        overlay_modify_expense.setVisibility(View.GONE);

        modifyExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_expense.setVisibility(View.VISIBLE);

                ExpenseDao expenseDao = AppRoomDatabase.getDatabase(getContext()).expenseDao();

                List<Expense> selectedExpenses = expenseDao.getSelectedExpenses();
                Expense currentExpense = selectedExpenses.get(0);

                String amount = currentExpense.amount;
                String tmp;
                if(inputCurrency.equalsIgnoreCase("€"))
                    tmp = amount.substring(0, amount.length()-1);
                else
                    tmp = amount.substring(1);
                editTextModifiedAmountSpent.setText(tmp);

                editTextModifiedCategory.setText(currentExpense.category);
                editTextModifiedDescription.setText(currentExpense.descprition);

                String date = currentExpense.date;
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

        backButtonModifyExpense = view.findViewById(R.id.backModificaSpesaButton);

        backButtonModifyExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_expense.setVisibility(View.GONE);
                Constants.hideKeyboard(view, requireActivity());
                addExpense.setVisibility(View.VISIBLE);
                modifyExpense.setVisibility(View.VISIBLE);
                deleteExpense.setVisibility(View.VISIBLE);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        saveModifiedExpense = view.findViewById(R.id.salvaModificaSpesa);
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
                ExpenseDao expenseDao = AppRoomDatabase.getDatabase(getContext()).expenseDao();

                List<Expense> selectedExpenses = expenseDao.getSelectedExpenses();
                Expense currentExpense = selectedExpenses.get(0);

                String amountText = currentExpense.amount;
                String amountSubstring;
                if(inputCurrency.equalsIgnoreCase("€"))
                    amountSubstring = amountText.substring(0, amountText.length()-1);
                else
                    amountSubstring = amountText.substring(1);
                double amount = Double.parseDouble(amountSubstring);
                spent = spent - amount;

                // Modifica il contenuto della card
                //TextView category = card.findViewById(R.id.categoryTextView);
                inputModifiedCategory = editTextModifiedCategory.getText().toString().trim();

                //TextView description = card.findViewById(R.id.descriptionTextView);
                inputModifiedDescription = editTextModifiedDescription.getText().toString().trim();

                //TextView dataSpesa = card.findViewById(R.id.dateTextView);
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
                    //category.setText(inputModifiedCategory);
                    currentExpense.setCategory(inputModifiedCategory);
                    expenseDao.updateCategory(currentExpense.id, inputModifiedCategory);
                    editTextModifiedDescription.setError(null);
                    //description.setText(inputModifiedDescription);
                    currentExpense.setDescprition(inputModifiedDescription);
                    expenseDao.updateDescription(currentExpense.id, inputModifiedDescription);
                    editTextModifiedDay.setError(null);
                    editTextModifiedMonth.setError(null);
                    editTextModifiedYear.setError(null);
                    String dataCompleta = getString(R.string.dataCompleta, inputModifiedDay, inputModifiedMonth, inputModifiedYear);
                    //dataSpesa.setText(dataCompleta);
                    currentExpense.setDate(dataCompleta);
                    expenseDao.updateDate(currentExpense.id, dataCompleta);
                    editTextModifiedAmountSpent.setError(null);

                    /*inputDay = inputModifiedDay;
                    inputMonth = inputModifiedMonth;
                    inputYear = inputModifiedYear;*/

                    double spesa = Double.parseDouble(inputModifiedAmountSpent);
                    updateProgressIndicator(spesa, budget, 1);
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = spesa+inputCurrency;
                    else
                        tmp = inputCurrency+spesa;
                    //amountTextView.setText(tmp);
                    currentExpense.setAmount(tmp);
                    expenseDao.updateAmount(currentExpense.id, tmp);

                    currentExpense.setSelected(false);
                    expenseDao.updateIsSelected(currentExpense.id, "0");
                    recyclerAdapter.notifyItemChanged(expenseList.indexOf(currentExpense));

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_modify_expense.setVisibility(View.GONE);
                    addExpense.setVisibility(View.VISIBLE);
                    addExpense.setEnabled(true);
                    editBudget.setEnabled(true);
                    filterButton.setEnabled(true);

                    /*MaterialCardView card = findSelectedCard(listExpenseCards);
                    card.setCardBackgroundColor(getResources().getColor(R.color.primary));
                    card.setStrokeColor(getResources().getColor(R.color.primary));
                    card.setSelected(false);*/

                }
            }
        });

        // Filter button
        overlay_filter = inflater.inflate(R.layout.overlay_filter, rootLayout, false);
        rootLayout.addView(overlay_filter);
        overlay_filter.setVisibility(View.GONE);

        filterButton = view.findViewById(R.id.buttonFilter);
        backButtonFilter = view.findViewById(R.id.backButtonFilter);
        saveCategory = view.findViewById(R.id.saveCategory);
        closeFilter = view.findViewById(R.id.closeFilter);
        filterText = view.findViewById(R.id.testoFiltro);
        editTextCategoryFilter = view.findViewById(R.id.inputCategoryFilter);
        editTextCategoryFilter.setAdapter(adapterCategory);

        backButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_filter.setVisibility(View.GONE);
                addExpense.setVisibility(View.VISIBLE);
                editBudget.setEnabled(true);
                filterButton.setEnabled(true);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_filter.setVisibility(View.VISIBLE);
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
                    filteredCards = filterByCategory(inputCategoryFilter);
                    //expenseCardsContainer.removeAllViews();
                    for (View card : filteredCards) {
                        //expenseCardsContainer.addView(card);
                    }
                    double countSpese = countAmountByCategory();
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = countSpese+inputCurrency;
                    else
                        tmp = inputCurrency+countSpese;
                    totExpense.setText(tmp);
                    overlay_filter.setVisibility(View.GONE);
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
                //expenseCardsContainer.removeAllViews();
                for (View card : listExpenseCards) {
                    //expenseCardsContainer.addView(card);
                }
                closeFilter.setVisibility(View.GONE);
                filterText.setVisibility(View.GONE);
                totExpense.setVisibility(View.GONE);
                addExpense.setEnabled(true);
            }
        });


        /*ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);*/

    }

    //aggiorna il progress indicator delle spese
    public void updateProgressIndicator(double spesa, int budget, int add){
        if(add == 1)
            spent = spent+spesa;
        // calcola la percentuale spesa
        progressPercentage = (int) ((spent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.error));
        else
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.secondary));
        // imposta il progress indicator
        progressIndicator.setProgress(progressPercentage);
        // aggiorna la descrizione del progress indicator

        formattedText = spent + " / " + budget + " spesi" + " (" + progressPercentage + "%)";
        progressText.setText(formattedText);
    }

    //filtra le carte per una determinata categoria
    public ArrayList<MaterialCardView> filterByCategory(String category){
        ArrayList<MaterialCardView> filteredCards = new ArrayList<>();
        for (MaterialCardView card : listExpenseCards) {
            TextView categoryTextView = card.findViewById(R.id.categoryTextView);
            if (categoryTextView != null && categoryTextView.getText().toString().equalsIgnoreCase(category)) {
                filteredCards.add(card);
            }
        }
        return filteredCards;
    }

    //calcola la spesa totale per una determinata categoria
    public double countAmountByCategory(){
        double spesaTot = 0;
        for (MaterialCardView card : filteredCards) {
            TextView amountTextView = card.findViewById(R.id.amountTextView);
            String amount = amountTextView.getText().toString().trim();
            String realAmount = amount.substring(0, amount.length()-1);
            double spesa = Double.parseDouble(realAmount);
            spesaTot += spesa;
        }

        return spesaTot;
    }

    //modifica la valuta nell'overlay_add_expense e nell'overlay_modify_expense
    public void updateCurrencyIcon(){
        TextInputLayout textQuantity = overlay_add_expense.findViewById(R.id.textFieldQuantita);
        TextInputLayout textModifyQuantity = overlay_modify_expense.findViewById(R.id.textFieldModificaQuantita);
        if(inputCurrency.equalsIgnoreCase("€")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
        }else if(inputCurrency.equalsIgnoreCase("$")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
        }else if(inputCurrency.equalsIgnoreCase("£")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
        }else if(inputCurrency.equalsIgnoreCase("¥")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
        }
    }


}
