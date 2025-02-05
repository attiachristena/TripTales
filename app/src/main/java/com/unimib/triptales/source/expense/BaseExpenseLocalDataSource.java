package com.unimib.triptales.source.expense;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.repository.expense.ExpenseResponseCallback;

import java.util.List;

public abstract class BaseExpenseLocalDataSource {
    protected ExpenseResponseCallback expenseCallback;

    public void setExpenseCallback(ExpenseResponseCallback expenseCallback){
        this.expenseCallback = expenseCallback;
    }

    public abstract void insertExpense(Expense expense);
    public abstract void updateExpense(Expense expense);
    public abstract void updateExpenseCategory(String expenseId, String newCategory);
    public abstract void updateExpenseDescription(String expenseId, String newDescription);
    public abstract void updateExpenseAmount(String expenseId, String newAmount);
    public abstract void updateExpenseDate(String expenseId, String newDate);
    public abstract void updateExpenseIsSelected(String expenseId, boolean newIsSelected);
    public abstract void deleteExpense(Expense expense);
    public abstract void deleteAllExpenses(List<Expense> expenses);
    public abstract void getAllExpenses();
    public abstract void getSelectedExpenses();
    public abstract void getFilteredExpenses(String category);
    //List<Expense> getAllExpensesByDiaryId(int diaryId);

}
