package com.unimib.triptales.repository.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Result;
import com.unimib.triptales.source.expense.BaseExpenseLocalDataSource;
import com.unimib.triptales.source.expense.BaseExpenseRemoteDataSource;

import java.util.Collections;
import java.util.List;

public class ExpenseRepository implements IExpenseRepository, ExpenseResponseCallback{

    private final BaseExpenseLocalDataSource expenseLocalDataSource;
    private final BaseExpenseRemoteDataSource expenseRemoteDataSource;
    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Expense>> selectedExpensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Expense>> filteredExpensesLiveData = new MutableLiveData<>();

    public ExpenseRepository(BaseExpenseLocalDataSource expenseLocalDataSource, BaseExpenseRemoteDataSource expenseRemoteDataSource) {
        this.expenseLocalDataSource = expenseLocalDataSource;
        this.expenseRemoteDataSource = expenseRemoteDataSource;
        this.expenseLocalDataSource.setExpenseCallback(this);
        this.expenseRemoteDataSource.setExpenseCallback(this);
    }

    public void insertExpense(Expense expense) {
        expenseLocalDataSource.insertExpense(expense);
        expenseRemoteDataSource.insertExpense(expense);
    }

    public void updateExpense(Expense expense) {
        expenseLocalDataSource.updateExpense(expense);
        expenseRemoteDataSource.updateExpense(expense);
    }

    public void updateExpenseCategory(String expenseId, String newCategory) {
        expenseLocalDataSource.updateExpenseCategory(expenseId, newCategory);
        expenseRemoteDataSource.updateExpenseCategory(expenseId, newCategory);
    }

    public void updateExpenseDescription(String expenseId, String newDescription) {
        expenseLocalDataSource.updateExpenseDescription(expenseId, newDescription);
        expenseRemoteDataSource.updateExpenseDescription(expenseId, newDescription);
    }

    public void updateExpenseAmount(String expenseId, String newAmount) {
        expenseLocalDataSource.updateExpenseAmount(expenseId, newAmount);
        expenseRemoteDataSource.updateExpenseAmount(expenseId, newAmount);
    }

    public void updateExpenseDate(String expenseId, String newDate) {
        expenseLocalDataSource.updateExpenseDate(expenseId, newDate);
        expenseRemoteDataSource.updateExpenseDate(expenseId, newDate);
    }

    public void updateExpenseIsSelected(String expenseId, boolean newIsSelected) {
        expenseLocalDataSource.updateExpenseIsSelected(expenseId, newIsSelected);
        expenseRemoteDataSource.updateExpenseIsSelected(expenseId, newIsSelected);
    }

    public void deleteExpense(Expense expense) {
        expenseLocalDataSource.deleteExpense(expense);
        expenseRemoteDataSource.deleteExpense(expense);
    }

    public void deleteAllExpenses(List<Expense> expenses) {
        expenseLocalDataSource.deleteAllExpenses(expenses);
        expenseRemoteDataSource.deleteAllExpenses(expenses);
    }

    public List<Expense> getAllExpenses() {
        expenseLocalDataSource.getAllExpenses();
        expenseRemoteDataSource.getAllExpenses();
        return expensesLiveData.getValue();
    }

    public List<Expense> getSelectedExpenses() {
        expenseLocalDataSource.getSelectedExpenses();
        expenseRemoteDataSource.getSelectedExpenses();
        return selectedExpensesLiveData.getValue();
    }

    public List<Expense> getFilteredExpenses(String category) {
        expenseLocalDataSource.getFilteredExpenses(category);
        expenseRemoteDataSource.getFilteredExpenses(category);
        return filteredExpensesLiveData.getValue();
    }

    @Override
    public void onSuccessFromRemote() {}

    @Override
    public void onSuccessFromRemote(List<Expense> expenses) {}

    @Override
    public void onFailureFromRemote(Exception exception) {}

    @Override
    public void onSuccessFromLocal() {}

    @Override
    public void onSuccessFromLocal(List<Expense> expenses) {
        expensesLiveData.setValue(expenses);
    }

    @Override
    public void onSuccessSelectionFromLocal(List<Expense> expenses) {
        selectedExpensesLiveData.setValue(expenses);
    }

    @Override
    public void onSuccessFilterFromLocal(List<Expense> expenses) {
        filteredExpensesLiveData.setValue(expenses);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {}
}
