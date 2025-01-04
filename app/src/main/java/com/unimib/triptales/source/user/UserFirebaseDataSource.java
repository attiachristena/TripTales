package com.unimib.triptales.source.user;

import static com.unimib.triptales.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.model.Diary;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();
    private final DatabaseReference databaseReference;

    public UserFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void saveDiary(String userId, Diary diary) {
        DatabaseReference userDiariesRef = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(userId)
                .child("diaries");

        String diaryId = userDiariesRef.push().getKey();
        if(diaryId != null){
            diary.id = Integer.parseInt(diaryId);
            userDiariesRef.child(diaryId).setValue(diary)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Diary saved successfully");
                        userResponseCallback.onSuccessSaveDiary(diary);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save diary", e);
                        userResponseCallback.onFailureDiaryOperation(e.getMessage());
                    });
        }
    }

    @Override
    public void getUserDiaries(String userId) {
        DatabaseReference userDiariesRef = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(userId)
                .child("diaries");

        userDiariesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Diary> diaries = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Diary diary = ds.getValue(Diary.class);
                    if (diary != null) {
                        diaries.add(diary);
                    }
                }
                userResponseCallback.onSuccessGetDiaries(diaries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch diaries", error.toException());
                userResponseCallback.onFailureDiaryOperation(error.getMessage());
            }
        });
    }

    @Override
    public void deleteUserDiary(String userId, String diaryId) {
        DatabaseReference diaryRef = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(userId)
                .child("diaries")
                .child(diaryId);

        diaryRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Diary deleted successfully");
                    userResponseCallback.onSuccessDeleteDiary(diaryId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete diary", e);
                    userResponseCallback.onFailureDiaryOperation(e.getMessage());
                });
    }
}
