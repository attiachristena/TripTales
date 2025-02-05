package com.unimib.triptales.repository.task;

import com.unimib.triptales.model.Task;

import java.util.List;

public interface TaskResponseCallback {
    void onSuccessFromRemote();
    void onSuccessFromRemote(List<Task> tasks);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal();
    void onSuccessFromLocal(List<Task> tasks);
    void onSuccessSelectionFromLocal(List<Task> tasks);
    void onFailureFromLocal(Exception exception);
}
