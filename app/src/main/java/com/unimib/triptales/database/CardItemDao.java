package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.unimib.triptales.model.CardItem;

import java.util.List;

@Dao
public interface CardItemDao {

    @Insert
    void insertCardItem(CardItem cardItem);

    @Query("SELECT * FROM CardItem WHERE checkpointDiaryId = :checkpointDiaryId")
    LiveData<List<CardItem>> getCardItemsByCheckpointDiaryId(int checkpointDiaryId);

    @Query("DELETE FROM CardItem WHERE checkpointDiaryId = :checkpointDiaryId")
    void deleteCardItemsByCheckpointDiaryId(int checkpointDiaryId);

    @Query("DELETE FROM CardItem")
    void deleteAllCardItems();

    @Query("DELETE FROM CardItem WHERE id = :id")
    void deleteCardItemById(int id);

    @Query("UPDATE CardItem SET title = :title, description = :description, date = :date, imageUri = :imageUri WHERE id = :id")
    void updateCardItem(int id, String title, String description, String date, String imageUri);

    @Query("UPDATE CardItem SET title = :title WHERE id = :id")
    void updateCardItemTitle(int id, String title);

    @Query("UPDATE CardItem SET description = :description WHERE id = :id")
    void updateCardItemDescription(int id, String description);

    @Query("UPDATE CardItem SET date = :date WHERE id = :id")
    void updateCardItemDate(int id, String date);

    @Query("UPDATE CardItem SET imageUri = :imageUri WHERE id = :id")
    void updateCardItemImageUri(int id, String imageUri);

    @Query("SELECT * FROM CardItem WHERE id = :id")
    LiveData<CardItem> getCardItemById(int id);

    @Query("SELECT * FROM CardItem")
    LiveData<List<CardItem>> getAllCardItems();
}
