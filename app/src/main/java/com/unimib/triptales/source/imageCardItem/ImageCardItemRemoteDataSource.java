package com.unimib.triptales.source.imageCardItem;

import static com.unimib.triptales.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.ImageCardItemDao;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageCardItemRemoteDataSource extends BaseImageCardItemRemoteDataSource {
    private final DatabaseReference databaseReference;

    public ImageCardItemRemoteDataSource(String userId, int checkpointId) {
        this.databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("checkpointDiaries")
                .child(String.valueOf(checkpointId))
                .child("imageCardItems");
    }

    public void insertImageCardItem(ImageCardItem imageCardItem) {
        if (imageCardItem != null) {
            Log.d("InsertImageCardItem", "CheckpointDiary ID: " + imageCardItem.getCheckpointDiaryId());

            databaseReference.child(String.valueOf(imageCardItem.getId())).setValue(imageCardItem)
                    .addOnSuccessListener(aVoid -> {
                        List<ImageCardItem> items = new ArrayList<>();
                        items.add(imageCardItem);
                        imageCardItemCallback.onSuccessFromRemote(items);
                    })
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void updateImageCardItem(ImageCardItem imageCardItem) {
        if (imageCardItem != null) {
            databaseReference.child(String.valueOf(imageCardItem.getId())).setValue(imageCardItem)
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void deleteImageCardItem(ImageCardItem imageCardItem) {
        if (imageCardItem != null) {
            databaseReference.child(String.valueOf(imageCardItem.getId())).removeValue()
                    .addOnSuccessListener(aVoid -> imageCardItemCallback.onSuccessDeleteFromRemote())
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void updateImageCardItemTitle(int id, String title) {
        if (title != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", title);

            databaseReference.child(String.valueOf(id)).updateChildren(updates)
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void updateImageCardItemDescription(int id, String description) {
        if (description != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("description", description);

            databaseReference.child(String.valueOf(id)).updateChildren(updates)
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void updateImageCardItemDate(int id, String date) {
        if (date != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("date", date);

            databaseReference.child(String.valueOf(id)).updateChildren(updates)
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void updateImageCardItemImageUri(int id, String imageUri) {
        if (imageUri != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("imageUri", imageUri);

            databaseReference.child(String.valueOf(id)).updateChildren(updates)
                    .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
        } else {
            imageCardItemCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    public void updateImageCardItemIsSelected(int id, boolean isSelected) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isSelected", isSelected);

        databaseReference.child(String.valueOf(id)).updateChildren(updates)
                .addOnFailureListener(e -> imageCardItemCallback.onFailureFromRemote(e));
    }


    public void getAllImageCardItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ImageCardItem> imageCardItemList = new ArrayList<>();
                        for (DataSnapshot imageCardItemSnapshot : snapshot.getChildren()) {
                            ImageCardItem imageCardItem = imageCardItemSnapshot.getValue(ImageCardItem.class);
                            if (imageCardItem != null) {
                                imageCardItemList.add(imageCardItem);
                            }
                        }

                        imageCardItemCallback.onSuccessFromRemote(imageCardItemList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        imageCardItemCallback.onFailureFromRemote(new Exception(error.getMessage()));
                    }
                });
    }

}
