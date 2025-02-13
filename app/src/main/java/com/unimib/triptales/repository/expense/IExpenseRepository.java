package com.unimib.triptales.repository.expense;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface IExpenseRepository {

    void insertExpense(Expense expense);
    void updateExpenseCategory(String expenseId, String newCategory);
    void updateExpenseDescription(String expenseId, String newDescription);
    void updateExpenseAmount(String expenseId, String newAmount);
    void updateExpenseDate(String expenseId, String newDate);
    void updateExpenseIsSelected(String expenseId, boolean newIsSelected);
    void deleteAllExpenses(List<Expense> expenses);
    List<Expense> getAllExpenses();
    List<Expense> getSelectedExpenses();
    List<Expense> getFilteredExpenses(String category);
}
