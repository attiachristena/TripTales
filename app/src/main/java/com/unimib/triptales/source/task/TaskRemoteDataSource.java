package com.unimib.triptales.source.task;

import static com.unimib.triptales.util.Constants.UNEXPECTED_ERROR;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRemoteDataSource extends BaseTaskRemoteDataSource{
    private final DatabaseReference databaseReference;

    public TaskRemoteDataSource(String userId) {
        this.databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("tasks");
    }

    @Override
    public void insertTask(Task task) {
        if (task != null) {
            databaseReference.child(task.getId()).setValue(task)
                    .addOnFailureListener(e -> taskCallback.onFailureFromRemote(e));
        } else {
            taskCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateAllTasks(List<Task> tasks) {
        if(tasks != null) {
            for(Task t : tasks) {
                databaseReference.child(t.getId()).setValue(t)
                        .addOnFailureListener(e -> taskCallback.onFailureFromRemote(e));
            }
        } else {
            taskCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateTaskName(String taskId, String newName) {
        if(newName != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);

            databaseReference.child(taskId).updateChildren(updates)
                    .addOnFailureListener(e -> taskCallback.onFailureFromRemote(e));
        } else {
            taskCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateTaskIsSelected(String taskId, boolean newIsSelected) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("task_isSelected", newIsSelected);

        databaseReference.child(taskId).updateChildren(updates)
                .addOnFailureListener(e -> taskCallback.onFailureFromRemote(e));
    }

    @Override
    public void updateTaskIsChecked(String taskId, boolean newIsChecked) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("task_isChecked", newIsChecked);

        databaseReference.child(taskId).updateChildren(updates)
                .addOnFailureListener(e -> taskCallback.onFailureFromRemote(e));
    }

    @Override
    public void deleteTask(Task task) {
        if(task != null){
            databaseReference.child(task.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> taskCallback.onSuccessDeleteFromRemote())
                    .addOnFailureListener(e -> taskCallback.onFailureFromRemote(e));
        } else {
            taskCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void deleteAllTasks(List<Task> tasks) {
        if(tasks != null){
            for(Task t : tasks){
                deleteTask(t);
            }
        }
    }

    @Override
    public void getAllTasks() {
        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskCallback.onSuccessFromRemote(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCallback.onFailureFromRemote(new Exception(error.getMessage()));
            }
        });
    }
}
