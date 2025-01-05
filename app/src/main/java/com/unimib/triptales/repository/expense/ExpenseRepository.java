package com.unimib.triptales.repository.expense;

import androidx.lifecycle.MutableLiveData;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Result;
import com.unimib.triptales.source.expense.BaseExpenseLocalDataSource;
import com.unimib.triptales.source.expense.BaseExpenseRemoteDataSource;

import java.util.List;

public class ExpenseRepository implements IExpenseRepository, ExpenseResponseCallback{

    private final BaseExpenseLocalDataSource expenseLocalDataSource;
    private final BaseExpenseRemoteDataSource expenseRemoteDataSource;

    private final MutableLiveData<Result> expenseMutableLiveData = new MutableLiveData<>();

    public ExpenseRepository(BaseExpenseLocalDataSource expenseLocalDataSource, BaseExpenseRemoteDataSource expenseRemoteDataSource) {
        this.expenseLocalDataSource = expenseLocalDataSource;
        this.expenseRemoteDataSource = expenseRemoteDataSource;
    }


    public long insertExpense(Expense expense) {
        return expenseLocalDataSource.insertExpense(expense);
    }

    public void updateExpense(Expense expense) {
        expenseLocalDataSource.updateExpense(expense);
    }

    public void updateExpenseCategory(int expenseId, String newCategory) {
        expenseLocalDataSource.updateExpenseCategory(expenseId, newCategory);
    }

    public void updateExpenseDescription(int expenseId, String newDescription) {
        expenseLocalDataSource.updateExpenseDescription(expenseId, newDescription);
    }

    public void updateExpenseAmount(int expenseId, String newAmount) {
        expenseLocalDataSource.updateExpenseAmount(expenseId, newAmount);
    }

    public void updateExpenseDate(int expenseId, String newDate) {
        expenseLocalDataSource.updateExpenseDate(expenseId, newDate);
    }

    public void updateExpenseIsSelected(int expenseId, boolean newIsSelected) {
        expenseLocalDataSource.updateExpenseIsSelected(expenseId, newIsSelected);
    }

    public void deleteExpense(Expense expense) {
        expenseLocalDataSource.deleteExpense(expense);
    }

    public void deleteAllExpenses(List<Expense> expenses) {
        expenseLocalDataSource.deleteAllExpenses(expenses);
    }

    public List<Expense> getAllExpenses() {
        return expenseLocalDataSource.getAllExpenses();
    }

    public List<Expense> getSelectedExpenses() {
        return expenseLocalDataSource.getSelectedExpenses();
    }

    public List<Expense> getFilteredExpenses(String category) {
        return expenseLocalDataSource.getFilteredExpenses(category);
    }


    @Override
    public void onSuccessSaveExpense(Expense expense) {

    }

    @Override
    public void onSuccessDeleteExpense(String expenseId) {

    }

    @Override
    public void onSuccessGetExpenses(List<Expense> expenses) {

    }

    @Override
    public void onFailureExpenseOperation(String message) {

    }
}
