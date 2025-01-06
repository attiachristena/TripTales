package com.unimib.triptales.repository.goal;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface GoalResponseCallback {

    //void onSuccessFromRemote(ArticleAPIResponse articleAPIResponse, long lastUpdate);
    //void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Expense> expenses);
    void onFailureFromLocal(Exception exception);
}
