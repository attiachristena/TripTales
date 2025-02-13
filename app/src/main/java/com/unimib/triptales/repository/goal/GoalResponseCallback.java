package com.unimib.triptales.repository.goal;

import com.unimib.triptales.model.Goal;

import java.util.List;

public interface GoalResponseCallback {
    void onSuccessDeleteFromRemote();
    void onSuccessFromRemote(List<Goal> goals);
    void onFailureFromRemote(Exception exception);

    void onSuccessDeleteFromLocal(List<Goal> goals);
    void onSuccessFromLocal(List<Goal> goals);
    void onSuccessSelectionFromLocal(List<Goal> goals);
    void onSuccessCheckedFromLocal(List<Goal> goals);
    void onFailureFromLocal(Exception exception);
}
