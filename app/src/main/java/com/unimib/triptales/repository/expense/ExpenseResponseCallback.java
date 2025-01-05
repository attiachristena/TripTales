package com.unimib.triptales.repository.expense;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface ExpenseResponseCallback {

    void onSuccessSaveExpense(Expense expense);
    void onSuccessDeleteExpense(String expenseId);
    void onSuccessGetExpenses(List<Expense> expenses);
    void onFailureExpenseOperation(String message);

}
