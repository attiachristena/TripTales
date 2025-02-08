package com.unimib.triptales.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.unimib.triptales.R;

import java.util.Objects;
import java.util.UUID;

@Entity(foreignKeys = @ForeignKey(entity = Diary.class, parentColumns = "id", childColumns = "diaryId", onDelete = CASCADE))
public class Expense {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "diaryId")
    private String diaryId;

    @ColumnInfo(name = "expense_amount")
    private String amount;

    @ColumnInfo(name = "expense_category")
    private String category;

    @ColumnInfo(name = "expense_description")
    private String description;

    @ColumnInfo(name = "expense_date")
    private String date;

    @ColumnInfo(name = "expense_isSelected")
    private boolean expense_isSelected;

    @ColumnInfo(name = "expense_iconId")
    private int iconId;

    public Expense(){}

    public Expense(String amount, String category, String description, String date, boolean expense_isSelected,
                   String diaryId) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        setCategory(category);
        this.description = description;
        this.date = date;
        this.expense_isSelected = expense_isSelected;
        this.diaryId = diaryId;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;

        if (category.equalsIgnoreCase("Shopping")){
            this.iconId = R.drawable.baseline_shopping_cart_24;
        } else if (category.equalsIgnoreCase("Cibo")){
            this.iconId = R.drawable.baseline_fastfood_24;
        } else if (category.equalsIgnoreCase("Trasporto")){
            this.iconId = R.drawable.baseline_directions_bus_24;
        } else if (category.equalsIgnoreCase("Alloggio")){
            this.iconId = R.drawable.baseline_hotel_24;
        } else if (category.equalsIgnoreCase("Cultura")){
            this.iconId = R.drawable.baseline_museum_24;
        } else if (category.equalsIgnoreCase("Svago")){
            this.iconId = R.drawable.baseline_attractions_24;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isExpense_isSelected() {
        return expense_isSelected;
    }

    public void setId(String id) { this.id = id; }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @NonNull
    public String getId() { return id;}

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getIconId() {
        return iconId;
    }

    public void setExpense_isSelected(boolean expense_isSelected) {
        this.expense_isSelected = expense_isSelected;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Expense expense = (Expense) obj;
        return id.equals(expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
