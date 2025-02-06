package com.unimib.triptales.source.diary;

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
                databaseReference.child(String.valueOf(diary.getId())).setValue(diary)
                        .addOnSuccessListener(aVoid -> diaryCallback.onSuccessFromRemote())
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception("Unexpected error"));
            }
        }

        @Override
        public void updateDiary(Diary diary) {
            if (diary != null) {
                databaseReference.child(String.valueOf(diary.getId())).setValue(diary)
                        .addOnSuccessListener(aVoid -> diaryCallback.onSuccessFromRemote())
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception("Unexpected error"));
            }
        }

        @Override
        public void updateDiaryName(int diaryId, String newName) {
            if (newName != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);
                databaseReference.child(String.valueOf(diaryId)).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> diaryCallback.onSuccessFromRemote())
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception("Unexpected error"));
            }
        }

        @Override
        public void updateDiaryIsSelected(int diaryId, boolean newIsSelected) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("isSelected", newIsSelected);
            databaseReference.child(String.valueOf(diaryId)).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> diaryCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
        }

        @Override
        public void deleteDiary(Diary diary) {
            if (diary != null) {
                databaseReference.child(String.valueOf(diary.getId())).removeValue()
                        .addOnSuccessListener(aVoid -> diaryCallback.onSuccessFromRemote())
                        .addOnFailureListener(e -> diaryCallback.onFailureFromRemote(e));
            } else {
                diaryCallback.onFailureFromRemote(new Exception("Unexpected error"));
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
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
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
                public void onCancelled(DatabaseError error) {
                    diaryCallback.onFailureFromRemote(new Exception(error.getMessage()));
                }
            });
        }

        @Override
        public void getSelectedDiaries() {
            databaseReference.orderByChild("isSelected").equalTo(true)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            List<Diary> selectedDiaries = new ArrayList<>();
                            for (DataSnapshot diarySnapshot : snapshot.getChildren()) {
                                Diary diary = diarySnapshot.getValue(Diary.class);
                                if (diary != null) {
                                    selectedDiaries.add(diary);
                                }
                            }
                            diaryCallback.onSuccessFromRemote(selectedDiaries);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            diaryCallback.onFailureFromRemote(new Exception(error.getMessage()));
                        }
                    });
        }

        @Override
        public void getAllDiariesByUserId(String userId) {
            databaseReference.orderByKey().equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            List<Diary> userDiaries = new ArrayList<>();
                            for (DataSnapshot diarySnapshot : snapshot.getChildren()) {
                                Diary diary = diarySnapshot.getValue(Diary.class);
                                if (diary != null) {
                                    userDiaries.add(diary);
                                }
                            }
                            diaryCallback.onSuccessFromRemote(userDiaries);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            diaryCallback.onFailureFromRemote(new Exception(error.getMessage()));
                        }
                    });
        }

    @Override
    public void updateDiaryStartDate(int diaryId, String newStartDate) {

    }

    @Override
    public void updateDiaryEndDate(int diaryId, String newEndDate) {

    }

    @Override
    public void updateDiaryCoverImage(int diaryId, String newCoverImage) {

    }

    @Override
    public void updateDiaryBudget(int diaryId, String newBudget) {

    }

    @Override
    public void updateDiaryCountry(int diaryId, String newCountry) {

    }


}
