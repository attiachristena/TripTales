package com.unimib.triptales.source.goal;

import static com.unimib.triptales.util.Constants.UNEXPECTED_ERROR;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.model.Expense;
import com.unimib.triptales.model.Goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalRemoteDataSource extends BaseGoalRemoteDataSource{
    private final DatabaseReference databaseReference;

    public GoalRemoteDataSource(String userId) {
        this.databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("goals");
    }

    @Override
    public void insertGoal(Goal goal) {
        if (goal != null) {
            databaseReference.child(goal.getId()).setValue(goal)
                    .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
        } else {
            goalCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateGoal(Goal goal) {
        if(goal != null) {
            databaseReference.child(goal.getId()).setValue(goal)
                    .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
        } else {
            goalCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateAllGoals(List<Goal> goals) {
        if(goals != null) {
            for (Goal g : goals) {
                updateGoal(g);
            }
        }
    }

    @Override
    public void updateGoalName(String goalId, String newName) {
        if(newName != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);

            databaseReference.child(goalId).updateChildren(updates)
                    .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
        } else {
            goalCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateGoalDescription(String goalId, String newDescription) {
        if(newDescription != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("description", newDescription);

            databaseReference.child(goalId).updateChildren(updates)
                    .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
        } else {
            goalCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateGoalIsSelected(String goalId, boolean newIsSelected) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("goal_isSelected", newIsSelected);

        databaseReference.child(goalId).updateChildren(updates)
                .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
    }

    @Override
    public void updateGoalIsChecked(String goalId, boolean newIsChecked) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("goal_isChecked", newIsChecked);

        databaseReference.child(goalId).updateChildren(updates)
                .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
    }

    @Override
    public void deleteGoal(Goal goal) {
        if(goal != null){
            databaseReference.child(goal.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> goalCallback.onSuccessDeleteFromRemote())
                    .addOnFailureListener(e -> goalCallback.onFailureFromRemote(e));
        } else {
            goalCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void deleteAllGoals(List<Goal> goals) {
        if(goals != null) {
            for (Goal g : goals) {
                deleteGoal(g);
            }
        }
    }

    @Override
    public void getAllGoals() {
        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Goal> goalList = new ArrayList<>();
                for (DataSnapshot goalSnapshot : snapshot.getChildren()) {
                    Goal goal = goalSnapshot.getValue(Goal.class);
                    if (goal != null) {
                        goalList.add(goal);
                    }
                }
                goalCallback.onSuccessFromRemote(goalList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                goalCallback.onFailureFromRemote(new Exception(error.getMessage()));
            }
        });
    }
}
