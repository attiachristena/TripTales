package com.unimib.triptales.source.expense;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface BaseExpenseLocalDataSource {

    long insertExpense(Expense expense);
    void updateExpense(Expense expense);
    void updateAllExpenses(List<Expense> expenses);
    void updateExpenseCategory(int expenseId, String newCategory);
    void updateExpenseDescription(int expenseId, String newDescription);
    void updateExpenseAmount(int expenseId, String newAmount);
    void updateExpenseDate(int expenseId, String newDate);
    void updateExpenseIsSelected(int expenseId, boolean newIsSelected);
    void deleteExpense(Expense expense);
    void deleteAllExpenses(List<Expense> expenses);
    List<Expense> getAllExpenses();
    List<Expense> getSelectedExpenses();
    List<Expense> getFilteredExpenses(String category);
    //List<Expense> getAllExpensesByDiaryId(int diaryId);

}
