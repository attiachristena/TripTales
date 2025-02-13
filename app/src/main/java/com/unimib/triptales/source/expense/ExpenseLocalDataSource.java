package com.unimib.triptales.source.expense;

import com.unimib.triptales.R;
import com.unimib.triptales.database.ExpenseDao;
import com.unimib.triptales.model.Expense;

import java.util.List;

public class ExpenseLocalDataSource extends BaseExpenseLocalDataSource {

    private final ExpenseDao expenseDao;
    private final String diaryId;

    public ExpenseLocalDataSource(ExpenseDao expenseDao, String diaryId) {
        this.expenseDao = expenseDao;
        this.diaryId = diaryId;
    }

    @Override
    public void insertExpense(Expense expense) {
        try{
            expenseDao.insert(expense);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseCategory(String expenseId, String newCategory) {
        try{
            if (newCategory.equalsIgnoreCase("Shopping")){
                expenseDao.updateIconId(expenseId, R.drawable.baseline_shopping_cart_24);
            } else if (newCategory.equalsIgnoreCase("Cibo")
                        || newCategory.equalsIgnoreCase("Food")){
                expenseDao.updateIconId(expenseId, R.drawable.baseline_fastfood_24);
            } else if (newCategory.equalsIgnoreCase("Trasporti")
                        || newCategory.equalsIgnoreCase("Transportation")){
                expenseDao.updateIconId(expenseId, R.drawable.baseline_directions_bus_24);
            } else if (newCategory.equalsIgnoreCase("Alloggio")
                        || newCategory.equalsIgnoreCase("Accommodation")){
                expenseDao.updateIconId(expenseId, R.drawable.baseline_hotel_24);
            } else if (newCategory.equalsIgnoreCase("Cultura")
                        || newCategory.equalsIgnoreCase("Culture")){
                expenseDao.updateIconId(expenseId, R.drawable.baseline_museum_24);
            } else if (newCategory.equalsIgnoreCase("Svago")
                        || newCategory.equalsIgnoreCase("Leisure")){
                expenseDao.updateIconId(expenseId, R.drawable.baseline_attractions_24);
            }
            expenseDao.updateCategory(expenseId, newCategory);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseDescription(String expenseId, String newDescription) {
        try{
            expenseDao.updateDescription(expenseId, newDescription);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseAmount(String expenseId, String newAmount) {
        try{
            expenseDao.updateAmount(expenseId, newAmount);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseDate(String expenseId, String newDate) {
        try{
            expenseDao.updateDate(expenseId, newDate);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void updateExpenseIsSelected(String expenseId, boolean newIsSelected) {
        try{
            expenseDao.updateIsSelected(expenseId, newIsSelected);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void deleteAllExpenses(List<Expense> expenses) {
        try{
            expenseDao.deleteAll(expenses);
            expenseCallback.onSuccessDeleteFromLocal(expenses);
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getAllExpenses() {
        try{
            expenseCallback.onSuccessFromLocal(expenseDao.getAll(diaryId));
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getSelectedExpenses() {
        try{
            expenseCallback.onSuccessSelectionFromLocal(expenseDao.getSelectedExpenses(diaryId));
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }

    @Override
    public void getFilteredExpenses(String category) {
        try{
            expenseCallback.onSuccessFilterFromLocal(expenseDao.getFilteredExpenses(diaryId, category));
        } catch (Exception e){
            expenseCallback.onFailureFromLocal(e);
        }
    }
}
