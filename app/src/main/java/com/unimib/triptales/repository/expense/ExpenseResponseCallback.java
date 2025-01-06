package com.unimib.triptales.repository.expense;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface ExpenseResponseCallback {

    //void onSuccessFromRemote(ArticleAPIResponse articleAPIResponse, long lastUpdate);
    //void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Expense> expenses);
    void onFailureFromLocal(Exception exception);

}
