package com.unimib.triptales.database;

import androidx.room.Embedded;

import com.unimib.triptales.model.Diary;

public class CompleteDiary {
    @Embedded
    public Diary diary;

   /* @Relation(parentColumn = "id", entityColumn = "diaryId")
    public List<Tappa> stages;  // Relazione con le tappe

    /*@Relation(parentColumn = "id", entityColumn = "diaryId")
    public List<Goal> goals;    // Relazione con gli obiettivi*/

    /*@Relation(parentColumn = "id", entityColumn = "diaryId")
    public List<Task> tasks;  // Relazione con le attività*/

    /*@Relation(parentColumn = "id", entityColumn = "diaryId")
    public List<Expense> expenses;    // Relazione con le spese*/
}
