package com.unimib.triptales.source.checkpointDiary;

import static com.unimib.triptales.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.model.CheckpointDiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckpointDiaryRemoteDataSource extends BaseCheckpointDiaryRemoteDataSource {
    private final DatabaseReference databaseReference;

    public CheckpointDiaryRemoteDataSource(String userId) {
        this.databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("checkpointDiaries");
    }

    @Override
    public void insertCheckpointDiary(CheckpointDiary checkpointDiary) {
        if (checkpointDiary != null) {
            databaseReference.child(String.valueOf(checkpointDiary.getId())).setValue(checkpointDiary)
                    .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
        } else {
            checkpointDiaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateCheckpointDiary(CheckpointDiary checkpointDiary) {
        if (checkpointDiary != null) {
            databaseReference.child(String.valueOf(checkpointDiary.getId())).setValue(checkpointDiary)
                    .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
        } else {
            checkpointDiaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateCheckpointDiaryName(int checkpointId, String newName) {
        if (newName != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);

            databaseReference.child(String.valueOf(checkpointId)).updateChildren(updates)
                    .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
        } else {
            checkpointDiaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateCheckpointDiaryDate(int checkpointId, String newDate) {
        if (newDate != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("date", newDate);

            databaseReference.child(String.valueOf(checkpointId)).updateChildren(updates)
                    .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
        } else {
            checkpointDiaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateCheckpointDiaryImageUri(int checkpointId, String newImageUri) {
        if (newImageUri != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("imageUri", newImageUri);

            databaseReference.child(String.valueOf(checkpointId)).updateChildren(updates)
                    .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
        } else {
            checkpointDiaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateCheckpointDiaryLatitude(int checkpointId, double newLatitude) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", newLatitude);

        databaseReference.child(String.valueOf(checkpointId)).updateChildren(updates)
                .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
    }

    @Override
    public void updateCheckpointDiaryLongitude(int checkpointId, double newLongitude) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("longitude", newLongitude);

        databaseReference.child(String.valueOf(checkpointId)).updateChildren(updates)
                .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
    }

    public void deleteCheckpointDiary(CheckpointDiary checkpointDiary) {
        if (checkpointDiary != null) {
            databaseReference.child(String.valueOf(checkpointDiary.getId())).removeValue()
                    .addOnSuccessListener(aVoid -> checkpointDiaryCallback.onSuccessDeleteFromRemote())
                    .addOnFailureListener(e -> checkpointDiaryCallback.onFailureFromRemote(e));
        } else {
            checkpointDiaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }


    @Override
    public void deleteAllCheckpointDiaries(List<CheckpointDiary> checkpointDiaries) {
        if (checkpointDiaries != null) {
            for (CheckpointDiary checkpoint : checkpointDiaries) {
                deleteCheckpointDiary(checkpoint);
            }
        }
    }

    @Override
    public void getAllCheckpointDiaries() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CheckpointDiary> checkpointList = new ArrayList<>();
                for (DataSnapshot checkpointSnapshot : snapshot.getChildren()) {
                    CheckpointDiary checkpoint = checkpointSnapshot.getValue(CheckpointDiary.class);
                    if (checkpoint != null) {
                        checkpointList.add(checkpoint);
                    }
                }
                checkpointDiaryCallback.onSuccessFromRemote(checkpointList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkpointDiaryCallback.onFailureFromRemote(new Exception(error.getMessage()));
            }
        });
    }
}