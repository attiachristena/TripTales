package com.unimib.triptales.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity/*(foreignKeys = @ForeignKey(entity = Diary.class, parentColumns = "id", childColumns = "diaryId", onDelete = CASCADE))*/
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public Task(String name, boolean isSelected, boolean isChecked) {
        this.name = name;
        this.isSelected = isSelected;
        this.isChecked = isChecked;
    }

    //public int diaryId; // ID del diario

    @ColumnInfo(name = "task_name")
    private String name;

    @ColumnInfo(name = "task_isSelected")
    private boolean isSelected;

    @ColumnInfo(name = "task_isChecked")
    private boolean isChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
