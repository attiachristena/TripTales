package com.unimib.triptales.repository.checkpointDiary;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unimib.triptales.database.CardItemDao;
import com.unimib.triptales.database.CheckpointDiaryDao;
import com.unimib.triptales.model.CheckpointDiary;

public class CheckpointDiaryRepository implements ICheckpointDiaryRepository {
    private CheckpointDiaryDao checkpointDiaryDao;
    private CardItemDao cardItemDao;

}
