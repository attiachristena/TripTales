package com.unimib.triptales.source.expense;

import com.unimib.triptales.R;
import com.unimib.triptales.database.ExpenseDao;
import com.unimib.triptales.model.Expense;

import java.util.List;

public class ExpenseLocalDataSource implements BaseExpenseLocalDataSource {

    private final ExpenseDao expenseDao;

    public ExpenseLocalDataSource(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    @Override
    public long insertExpense(Expense expense) {
        return expenseDao.insert(expense);
    }

    @Override
    public void updateExpense(Expense expense) {
        expenseDao.update(expense);
    }

    @Override
    public void updateAllExpenses(List<Expense> expenses) {
        expenseDao.updateAll(expenses);
    }

    @Override
    public void updateExpenseCategory(int expenseId, String newCategory) {
        if (newCategory.equalsIgnoreCase("Shopping")){
            expenseDao.updateIconId(expenseId, R.drawable.baseline_shopping_cart_24);
        } else if (newCategory.equalsIgnoreCase("Cibo")){
            expenseDao.updateIconId(expenseId, R.drawable.baseline_fastfood_24);
        } else if (newCategory.equalsIgnoreCase("Trasporto")){
            expenseDao.updateIconId(expenseId, R.drawable.baseline_directions_bus_24);
        } else if (newCategory.equalsIgnoreCase("Alloggio")){
            expenseDao.updateIconId(expenseId, R.drawable.baseline_hotel_24);
        } else if (newCategory.equalsIgnoreCase("Cultura")){
            expenseDao.updateIconId(expenseId, R.drawable.baseline_museum_24);
        } else if (newCategory.equalsIgnoreCase("Svago")){
            expenseDao.updateIconId(expenseId, R.drawable.baseline_attractions_24);
        }
        expenseDao.updateCategory(expenseId, newCategory);
    }

    @Override
    public void updateExpenseDescription(int expenseId, String newDescription) {
        expenseDao.updateDescription(expenseId, newDescription);
    }

    @Override
    public void updateExpenseAmount(int expenseId, String newAmount) {
        expenseDao.updateAmount(expenseId, newAmount);
    }

    @Override
    public void updateExpenseDate(int expenseId, String newDate) {
        expenseDao.updateDate(expenseId, newDate);
    }

    @Override
    public void updateExpenseIsSelected(int expenseId, boolean newIsSelected) {
        expenseDao.updateIsSelected(expenseId, newIsSelected);
    }

    @Override
    public void deleteExpense(Expense expense) {
        expenseDao.delete(expense);
    }

    @Override
    public void deleteAllExpenses(List<Expense> expenses) {
        expenseDao.deleteAll(expenses);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return expenseDao.getAll();
    }

    @Override
    public List<Expense> getSelectedExpenses() {
        return expenseDao.getSelectedExpenses();
    }

    @Override
    public List<Expense> getFilteredExpenses(String category) {
        return expenseDao.getFilteredExpenses(category);
    }
}
