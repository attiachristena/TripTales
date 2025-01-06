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
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Update
    void updateAll(List<Expense> expenses);

    @Query("UPDATE Expense SET expense_category = :newCategory WHERE id = :expenseId")
    void updateCategory(int expenseId, String newCategory);

    @Query("UPDATE Expense SET expense_description = :newDescription WHERE id = :expenseId")
    void updateDescription(int expenseId, String newDescription);

    @Query("UPDATE Expense SET expense_amount = :newAmount WHERE id = :expenseId")
    void updateAmount(int expenseId, String newAmount);

    @Query("UPDATE Expense SET expense_date = :newDate WHERE id = :expenseId")
    void updateDate(int expenseId, String newDate);

    @Query("UPDATE Expense SET expense_isSelected = :newIsSelected WHERE id = :expenseId")
    void updateIsSelected(int expenseId, boolean newIsSelected);

    @Delete
    void delete(Expense expense);

    @Delete
    void deleteAll(List<Expense> expenses);

    @Query("SELECT * FROM Expense")
    List<Expense> getAll();

    @Query("SELECT * FROM Expense WHERE expense_isSelected = 1")
    List<Expense> getSelectedExpenses();

    @Query("SELECT * FROM Expense WHERE expense_category = :category")
    List<Expense> getFilteredExpenses(String category);

    /*//Recupero delle spese di un determinato diario
    @Query("SELECT * FROM Expense WHERE diaryId = :diaryId")
    List<Expense> getAllByDiaryId(int diaryId);*/
}

