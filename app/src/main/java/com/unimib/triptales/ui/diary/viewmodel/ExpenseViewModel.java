package com.unimib.triptales.ui.diary.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.repository.expense.ExpenseRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;

import java.util.ArrayList;
import java.util.List;

public class ExpenseViewModel extends ViewModel {

    private final IExpenseRepository expenseRepository;

    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    public ExpenseViewModel(IExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public MutableLiveData<List<Expense>> getExpensesLiveData() {
        return expensesLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public void fetchAllExpenses() {
        loadingLiveData.setValue(true);
        try {
            List<Expense> expenses = expenseRepository.getAllExpenses();
            expensesLiveData.postValue(expenses);
            loadingLiveData.postValue(false);
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public long insertExpense(Expense expense) {
        loadingLiveData.setValue(true);
        long id = 0;
        try {
            id = expenseRepository.insertExpense(expense);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nell'inserimento della spesa: "+e.getMessage());
        }
        return id;
    }

    public void updateExpense(Expense expense) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpense(expense);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nell'aggiornamento della spesa: "+e.getMessage());
        }
    }

    public void updateExpenseCategory(int expenseId, String newCategory) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseCategory(expenseId, newCategory);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della categoria della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseDescription(int expenseId, String newDescription) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseDescription(expenseId, newDescription);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della descrizione della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseAmount(int expenseId, String newAmount) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseAmount(expenseId, newAmount);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento dell'importo della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseDate(int expenseId, String newDate) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseDate(expenseId, newDate);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della data della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseIsSelected(int expenseId, boolean newIsSelected) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseIsSelected(expenseId, newIsSelected);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento di isSelected: " + e.getMessage());
        }
    }

    public void deleteExpense(Expense expense) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.deleteExpense(expense);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione della spesa: "+e.getMessage());
        }
    }

    public void deleteAllExpenses(List<Expense> expenses) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.deleteAllExpenses(expenses);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione della lista di spese: "+e.getMessage());
        }
    }

    public List<Expense> getAllExpenses() {
        loadingLiveData.setValue(true);
        List<Expense> expenses = new ArrayList<>();
        try {
            expenses = expenseRepository.getAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione di tutte le spese: "+e.getMessage());
        }
        return expenses;
    }

    public List<Expense> getSelectedExpenses() {
        loadingLiveData.setValue(true);
        List<Expense> expenses = new ArrayList<>();
        try {
            expenses = expenseRepository.getSelectedExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione delle spese selezionate: "+e.getMessage());
        }
        return expenses;
    }

    public List<Expense> getFilteredExpenses(String category) {
        loadingLiveData.setValue(true);
        List<Expense> expenses = new ArrayList<>();
        try {
            expenses = expenseRepository.getFilteredExpenses(category);
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione delle spese filtrate: "+e.getMessage());
        }
        return expenses;
    }
}
