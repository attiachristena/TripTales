package com.unimib.triptales.repository.expense;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface ExpenseResponseCallback {
    void onSuccessDeleteFromRemote();
    void onSuccessFromRemote(List<Expense> expenses);
    void onFailureFromRemote(Exception exception);

    void onSuccessDeleteFromLocal(List<Expense> expenses);
    void onSuccessFromLocal(List<Expense> expenses);
    void onSuccessSelectionFromLocal(List<Expense> expenses);
    void onSuccessFilterFromLocal(List<Expense> expenses);
    void onFailureFromLocal(Exception exception);

}
