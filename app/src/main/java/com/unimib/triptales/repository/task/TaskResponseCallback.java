package com.unimib.triptales.repository.task;

import com.unimib.triptales.model.Expense;

import java.util.List;

public interface TaskResponseCallback {
    //void onSuccessFromRemote(ArticleAPIResponse articleAPIResponse, long lastUpdate);
    //void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Expense> expenses);
    void onFailureFromLocal(Exception exception);
}
