package com.unimib.triptales.source.expense;

import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.ExpenseDao;
import com.unimib.triptales.model.Expense;

import java.util.List;

public class ExpenseLocalDataSource extends BaseExpenseLocalDataSource {

    private final ExpenseDao expenseDao;

    public ExpenseLocalDataSource(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    @Override
    public void insertExpense(Expense expense) {
        try{
            expenseDao.insert(expense);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpense(Expense expense) {
        try{
            expenseDao.update(expense);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseCategory(String expenseId, String newCategory) {
        try{
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
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseDescription(String expenseId, String newDescription) {
        try{
            expenseDao.updateDescription(expenseId, newDescription);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseAmount(String expenseId, String newAmount) {
        try{
            expenseDao.updateAmount(expenseId, newAmount);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseDate(String expenseId, String newDate) {
        try{
            expenseDao.updateDate(expenseId, newDate);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseIsSelected(String expenseId, boolean newIsSelected) {
        try{
            expenseDao.updateIsSelected(expenseId, newIsSelected);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteExpense(Expense expense) {
        try{
            expenseDao.delete(expense);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteAllExpenses(List<Expense> expenses) {
        try{
            expenseDao.deleteAll(expenses);
            expenseCallback.onSuccessFromLocal();
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getAllExpenses() {
        try{
            expenseCallback.onSuccessFromLocal(expenseDao.getAll());
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getSelectedExpenses() {
        try{
            expenseCallback.onSuccessSelectionFromLocal(expenseDao.getSelectedExpenses());
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getFilteredExpenses(String category) {
        try{
            expenseCallback.onSuccessFilterFromLocal(expenseDao.getFilteredExpenses(category));
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }
}
