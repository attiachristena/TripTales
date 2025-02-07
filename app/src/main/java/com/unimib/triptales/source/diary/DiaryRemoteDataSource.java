package com.unimib.triptales.source.diary;

import static com.unimib.triptales.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.model.Diary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiaryRemoteDataSource extends BaseDiaryRemoteDataSource {

        private final DatabaseReference databaseReference;

        public DiaryRemoteDataSource(String userId) {
            this.databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("diaries");
        }

    @Override
        public void insertDiary(Diary diary) {
            if (diary != null) {
                databaseReference.child(diary.getId()).setValue(diary)
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
            }
        }

        @Override
        public void updateDiary(Diary diary) {
            if (diary != null) {
                databaseReference.child(diary.getId()).setValue(diary)
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
            }
        }

        @Override
        public void updateDiaryName(String diaryId, String newName) {
            if (newName != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);
                databaseReference.child(diaryId).updateChildren(updates)
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
            }
        }

    @Override
    public void updateDiaryStartDate(String diaryId, String newStartDate) {
        if (newStartDate != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("startDate", newStartDate);
            databaseReference.child(diaryId).updateChildren(updates)
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        } else {
            diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateDiaryEndDate(String diaryId, String newEndDate) {
        if (newEndDate != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("endDate", newEndDate);
            databaseReference.child(diaryId).updateChildren(updates)
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        } else {
            diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateDiaryCoverImage(String diaryId, String newCoverImage) {
        if (newCoverImage != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("coverImageUri", newCoverImage);
            databaseReference.child(diaryId).updateChildren(updates)
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        } else {
            diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateDiaryBudget(String diaryId, String newBudget) {
        if (newBudget != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("budget", newBudget);
            databaseReference.child(diaryId).updateChildren(updates)
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        } else {
            diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateDiaryCountry(String diaryId, String newCountry) {
        if (newCountry != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("country", newCountry);
            databaseReference.child(diaryId).updateChildren(updates)
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        } else {
            diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

        @Override
        public void updateDiaryIsSelected(String diaryId, boolean newIsSelected) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("diary_isSelected", newIsSelected);
            databaseReference.child(String.valueOf(diaryId)).updateChildren(updates)
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        }

        @Override
        public void deleteDiary(Diary diary) {
            if (diary != null) {
                databaseReference.child(diary.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> diaryCallback.onSuccessDeleteFromRemote())
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
            }
        }

        @Override
        public void deleteAllDiaries(List<Diary> diaries) {
            if (diaries != null) {
                for (Diary d : diaries) {
                    deleteDiary(d);
                }
            }
        }

        @Override
        public void getAllDiaries() {
            databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Diary> diaryList = new ArrayList<>();
                    for (DataSnapshot diarySnapshot : snapshot.getChildren()) {
                        Diary diary = diarySnapshot.getValue(Diary.class);
                        if (diary != null) {
                            diaryList.add(diary);
                        }
                    }
                    diaryCallback.onSuccessFromRemote(diaryList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    diaryCallback.onFailureFromRemote(new Exception(error.getMessage()));
                }
            });
        }

}
