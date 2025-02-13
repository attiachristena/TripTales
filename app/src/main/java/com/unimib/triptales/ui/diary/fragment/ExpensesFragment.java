package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.UPDATED;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.unimib.triptales.ui.diary.overlay.OverlayAddEditExpense;
import com.unimib.triptales.ui.diary.overlay.OverlayBudget;
import com.unimib.triptales.ui.diary.overlay.OverlayDelete;
import com.unimib.triptales.ui.diary.overlay.OverlayFilter;
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
    private LinearProgressIndicator progressIndicator;
    private TextView budgetTextView;
    private FloatingActionButton addExpenseButton;
    private ConstraintLayout expenseRootLayout;
    private FloatingActionButton editExpenseButton;
    private FloatingActionButton deleteExpenseButton;
    private OverlayAddEditExpense overlayExpense;
    private OverlayBudget overlayBudget;
    private Button filterButton;
    private ImageButton closeFilterButton;
    private TextView filterTextView;
    private OverlayFilter overlayFilter;
    private String inputCurrency;
    private TextView noExpensesTextView;
    private ExpenseViewModel expenseViewModel;
    private HomeViewModel homeViewModel;
    private ExpensesRecyclerAdapter expensesRecyclerAdapter;
    private boolean bEdit;
    private boolean bAdd;
    private TextInputLayout currencyTextInputLayout;
    private OverlayDelete overlayDelete;
    private FrameLayout darkBackground;

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
                new ViewModelFactory(expenseRepository, requireActivity().getApplication())).get(ExpenseViewModel.class);

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
            if(expenseViewModel.getBFilter().getValue() != null && !expenseViewModel.getBFilter().getValue()) {
                expenseViewModel.toggleExpenseSelection(expense);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expenseRootLayout = view.findViewById(R.id.rootLayoutSpese);
        ConstraintLayout diaryRootLayout = requireActivity().findViewById(R.id.rootLayoutDiary);
        noExpensesTextView = view.findViewById(R.id.noSpeseString);
        darkBackground = requireActivity().findViewById(R.id.dark_background);
        bEdit = false;
        bAdd = false;

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

        // gestione budget
        overlayBudget = new OverlayBudget(requireContext(), diaryRootLayout, inputCurrency, expenseViewModel);

        ImageButton editBudgetButton = view.findViewById(R.id.editBudget);
        editBudgetButton.setOnClickListener(editBudgetButtonListener -> {
            overlayBudget.showOverlay(homeViewModel);
            currencyTextInputLayout = requireActivity().findViewById(R.id.textFieldCurrency);
            List<Expense> expenses = expenseViewModel.getAllExpenses();
            if(expenses != null) {
                if(expenses.isEmpty()) {
                    currencyTextInputLayout.setEnabled(true);
                } else {
                    currencyTextInputLayout.setEnabled(false);
                    currencyTextInputLayout.setBoxBackgroundColor
                            (ContextCompat.getColor(requireContext(), R.color.background_overlays));
                }
            }
        });

        filterButton = view.findViewById(R.id.buttonFilter);
        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            if(expenses != null) {
                expensesRecyclerAdapter.setExpenseList(expenses);
                if (expenses.isEmpty()) {
                    noExpensesTextView.setVisibility(View.VISIBLE);
                    filterButton.setEnabled(false);
                } else {
                    noExpensesTextView.setVisibility(View.GONE);
                    filterButton.setEnabled(true);
                }
            }
        });

        TextView totExpenseTextView = view.findViewById(R.id.totSpesa);
        homeViewModel.getBudgetLiveData().observe(getViewLifecycleOwner(), diaryBudget -> {
            if(diaryBudget != null) {
                inputCurrency = expenseViewModel.getInputCurrency(diaryBudget);
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
            }
        });

        expenseViewModel.getBudgetOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if (visible) {
                darkBackground.setClickable(true);
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                Constants.hideKeyboard(view, requireActivity());
                darkBackground.setClickable(false);
                darkBackground.setVisibility(View.GONE);
            }
        });

        expenseViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if(errorMessage != null){
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        //gestione dell'aggiunta e la modifica di una spesa
        addExpenseButton = view.findViewById(R.id.addButtonSpese);
        editExpenseButton = view.findViewById(R.id.modificaSpesa);
        deleteExpenseButton = view.findViewById(R.id.eliminaSpesa);

        overlayExpense = new OverlayAddEditExpense(diaryRootLayout, requireContext(),
                expenseViewModel);

        addExpenseButton.setOnClickListener(addExpenseButtonListener -> {
            if(budget == 0) {
                Snackbar snackbar = Snackbar.make(expenseRootLayout, R.string.snackbarErroreBudget,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                bAdd = true;
                expenseViewModel.setExpenseOverlayVisibility(true);
                overlayExpense.showOverlay(bAdd, bEdit, inputCurrency);
            }
        });

        expenseViewModel.getExpenseOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if (visible) {
                darkBackground.setClickable(true);
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                Constants.hideKeyboard(view, requireActivity());
                darkBackground.setClickable(false);
                darkBackground.setVisibility(View.GONE);
                if(bAdd){
                    bAdd = false;
                } else if(bEdit){
                    bEdit = false;
                }
            }
        });

        expenseViewModel.getSelectedExpensesLiveData().observe(getViewLifecycleOwner(), selectedExpenses -> {
            if(selectedExpenses != null) {
                if (selectedExpenses.size() == 1) {
                    if(Boolean.TRUE.equals(expenseViewModel.getExpenseOverlayVisibility().getValue())){
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

        // gestione dell'eliminazione di una spesa
        overlayDelete = new OverlayDelete(diaryRootLayout, requireContext(), expenseViewModel);

        expenseViewModel.getDeleteOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                darkBackground.setClickable(true);
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                Constants.hideKeyboard(view, requireActivity());
                darkBackground.setClickable(false);
                darkBackground.setVisibility(View.GONE);
                List<Expense> selectedExpenses = expenseViewModel.getSelectedExpensesLiveData().getValue();
                if(selectedExpenses != null && !selectedExpenses.isEmpty()){
                    addExpenseButton.setEnabled(false);
                }
            }
        });

        deleteExpenseButton.setOnClickListener(deleteExpenseButtonListener -> {
            expenseViewModel.setDeleteOverlayVisibility(true);
            overlayDelete.showOverlay();
            TextView deleteText = requireActivity().findViewById(R.id.deleteText);
            TextView deleteDescriptionText = requireActivity().findViewById(R.id.deleteDescriptionText);
            deleteText.setText(R.string.delete_expense);
            deleteDescriptionText.setText(R.string.delete_expense_description);
        });

        editExpenseButton.setOnClickListener(editExpenseButtonListener -> {
            bEdit = true;
            expenseViewModel.setExpenseOverlayVisibility(true);
            overlayExpense.showOverlay(bAdd, bEdit, inputCurrency);
        });

        // gestione del filtro delle spese
        overlayFilter = new OverlayFilter(requireContext(), diaryRootLayout, expenseViewModel);

        closeFilterButton = view.findViewById(R.id.closeFilter);
        filterTextView = view.findViewById(R.id.testoFiltro);

        filterButton.setOnClickListener(filterButtonListener -> {
            expenseViewModel.setFilterOverlayVisibility(true);
            overlayFilter.showOverlay();
        });

        expenseViewModel.getFilterOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                darkBackground.setClickable(true);
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                Constants.hideKeyboard(view, requireActivity());
                darkBackground.setClickable(false);
                darkBackground.setVisibility(View.GONE);
                boolean filter = false;
                if(expenseViewModel.getBFilter().getValue() != null) {
                    filter = expenseViewModel.getBFilter().getValue();
                }
                if(filter){
                    closeFilterButton.setVisibility(View.VISIBLE);
                    filterTextView.setVisibility(View.VISIBLE);
                    totExpenseTextView.setVisibility(View.VISIBLE);
                    addExpenseButton.setEnabled(false);
                }
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

        closeFilterButton.setOnClickListener(closeFilterButtonListener -> {
            expensesRecyclerAdapter.setExpenseList(expenseViewModel.getAllExpenses());
            closeFilterButton.setVisibility(View.GONE);
            filterTextView.setVisibility(View.GONE);
            totExpenseTextView.setVisibility(View.GONE);
            addExpenseButton.setEnabled(true);
            expenseViewModel.setBFilter(false);
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
}
