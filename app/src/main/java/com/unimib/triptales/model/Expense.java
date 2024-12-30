package com.unimib.triptales.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.unimib.triptales.R;

import java.util.Objects;

@Entity/*(foreignKeys = @ForeignKey(entity = Diary.class, parentColumns = "id", childColumns = "diaryId", onDelete = CASCADE))*/
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public Expense(String amount, String category, String descprition, String date, boolean isSelected) {
        this.amount = amount;
        setCategory(category);
        this.descprition = descprition;
        this.date = date;
    }

    public void updateCategory(String newCategory){
        this.category = newCategory;
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

    public void setDescprition(String descprition) {
        this.descprition = descprition;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    //public int diaryId;

    @ColumnInfo(name = "expense_amount")
    public String amount;

    @ColumnInfo(name = "expense_category")
    public String category;

    @ColumnInfo(name = "expense_description")
    public String descprition;

    @ColumnInfo(name = "expense_date")
    public String date;

    @ColumnInfo(name = "isSelected")
    public boolean isSelected;

    @ColumnInfo(name = "iconId")
    public int iconId;

}
