package com.unimib.triptales.repository.task;

import com.unimib.triptales.model.Task;

import java.util.List;

public interface TaskResponseCallback {
    void onSuccessDeleteFromRemote();
    void onSuccessFromRemote(List<Task> tasks);
    void onFailureFromRemote(Exception exception);

    void onSuccessDeleteFromLocal(List<Task> tasks);
    void onSuccessFromLocal(List<Task> tasks);
    void onSuccessSelectionFromLocal(List<Task> tasks);
    void onFailureFromLocal(Exception exception);
}
