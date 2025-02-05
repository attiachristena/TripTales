package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.triptales.model.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Query("UPDATE Expense SET expense_category = :newCategory WHERE id = :expenseId")
    void updateCategory(String expenseId, String newCategory);

    @Query("UPDATE Expense SET expense_description = :newDescription WHERE id = :expenseId")
    void updateDescription(String expenseId, String newDescription);

    @Query("UPDATE Expense SET expense_amount = :newAmount WHERE id = :expenseId")
    void updateAmount(String expenseId, String newAmount);

    @Query("UPDATE Expense SET expense_date = :newDate WHERE id = :expenseId")
    void updateDate(String expenseId, String newDate);

    @Query("UPDATE Expense SET expense_isSelected = :newIsSelected WHERE id = :expenseId")
    void updateIsSelected(String expenseId, boolean newIsSelected);

    @Query("UPDATE Expense SET expense_iconId = :newIconId WHERE id = :expenseId")
    void updateIconId(String expenseId, int newIconId);

    @Delete
    void delete(Expense expense);

    @Delete
    void deleteAll(List<Expense> expenses);

    @Query("SELECT * FROM Expense ORDER BY STRFTIME('%Y-%m-%d', \n" +
            "                 SUBSTR(expense_date, 7, 4) || '-' || \n" +
            "                 SUBSTR(expense_date, 4, 2) || '-' || \n" +
            "                 SUBSTR(expense_date, 1, 2)) DESC")
    List<Expense> getAll();

    @Query("SELECT * FROM Expense WHERE expense_isSelected = 1")
    List<Expense> getSelectedExpenses();

    @Query("SELECT * FROM Expense WHERE expense_category = :category ORDER BY STRFTIME('%Y-%m-%d', \n" +
            "                 SUBSTR(expense_date, 7, 4) || '-' || \n" +
            "                 SUBSTR(expense_date, 4, 2) || '-' || \n" +
            "                 SUBSTR(expense_date, 1, 2)) DESC")
    List<Expense> getFilteredExpenses(String category);

    /*//Recupero delle spese di un determinato diario
    @Query("SELECT * FROM Expense WHERE diaryId = :diaryId")
    List<Expense> getAllByDiaryId(int diaryId);*/
}

