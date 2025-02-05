package com.unimib.triptales.ui.diary.viewmodel;

import static com.unimib.triptales.util.Constants.CURRENCY_EUR;
import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.UPDATED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.triptales.model.Expense;
import com.unimib.triptales.repository.expense.IExpenseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseViewModel extends ViewModel {

    private final IExpenseRepository expenseRepository;

    private MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Expense>> selectedExpensesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Expense>> filteredExpensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> amountSpentLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> budgetOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<Boolean> expenseOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<Boolean> filterOverlayVisibility = new MutableLiveData<>();
    private final MutableLiveData<String> expenseEvent = new MutableLiveData<>();

    public ExpenseViewModel(IExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public LiveData<List<Expense>> getExpensesLiveData() {
        return expensesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<List<Expense>> getSelectedExpensesLiveData() {
        return selectedExpensesLiveData;
    }

    public LiveData<Double> getAmountSpentLiveData() {
        return amountSpentLiveData;
    }

    public LiveData<List<Expense>> getFilteredExpensesLiveData() {
        return filteredExpensesLiveData;
    }

    public MutableLiveData<String> getExpenseEvent() {
        return expenseEvent;
    }

    public MutableLiveData<Boolean> getBudgetOverlayVisibility() {
        return budgetOverlayVisibility;
    }

    public void setBudgetOverlayVisibility(boolean visible) {
        budgetOverlayVisibility.postValue(visible);
    }

    public MutableLiveData<Boolean> getExpenseOverlayVisibility() {
        return expenseOverlayVisibility;
    }

    public void setExpenseOverlayVisibility(boolean visible) {
        expenseOverlayVisibility.postValue(visible);
    }

    public MutableLiveData<Boolean> getFilterOverlayVisibility() {
        return filterOverlayVisibility;
    }

    public void setFilterOverlayVisibility(boolean visible) {
        filterOverlayVisibility.postValue(visible);
    }

    public void filterExpenses(String category, String currency){
        if (category == null || category.trim().isEmpty()) {
            errorLiveData.setValue("Scegli una categoria");
            return;
        }

        List<Expense> filteredExpenses = getFilteredExpenses(category);
        if (filteredExpenses != null) {
            filteredExpensesLiveData.postValue(filteredExpenses);
        } else {
            errorLiveData.setValue("Nessuna spesa trovata per la categoria specificata");
        }
    }

    public void fetchAllExpenses() {
        expensesLiveData.setValue(expenseRepository.getAllExpenses());
    }

    public boolean validateInputExpense(String amount, String category, String description,
                                        String day, String month, String year) {
        boolean correct = true;
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int inputDay = 0, inputMonth= 0, inputYear = 0;
        if(!day.isEmpty()) inputDay = Integer.parseInt(day);
        if(!month.isEmpty()) inputMonth = Integer.parseInt(month);
        if(!year.isEmpty()) inputYear = Integer.parseInt(year);
        if (amount.isEmpty()) {
            errorLiveData.setValue("Inserisci un importo");
        } else if (category.isEmpty()) {
            errorLiveData.setValue("Scegli una categoria");
        } else if (description.isEmpty()) {
            errorLiveData.setValue("Inserisci una descrizione");
        } else if(day.isEmpty()) {
            errorLiveData.setValue("Inserisci un giorno");
        } else if (inputDay < 1 || inputDay > 31) {
            errorLiveData.setValue("Giorno inserito non corretto");
        } else if(month.isEmpty()){
            errorLiveData.setValue("Inserisci un mese");
        } else if(inputMonth < 1 || inputMonth > 12){
            errorLiveData.setValue("Mese inserito non corretto");
        } else if(year.isEmpty()){
            errorLiveData.setValue("Inserisci un anno");
        } else if(inputYear < 2000 || inputYear > 2100){
            errorLiveData.setValue("Anno inserito non corretto");
        } else {
            errorLiveData.setValue(null);
        }

        if (inputMonth == 2 && isLeapYear(inputYear)) {
            daysInMonth[1] = 29;
        }

        if(inputDay > daysInMonth[inputMonth - 1]){
            errorLiveData.setValue("Giorno inserito non corretto");
        }

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public boolean validateInputBudget(String budget, String currency){
        boolean correct = true;
        if(budget.isEmpty()){
            errorLiveData.setValue("Inserisci un budget");
        } else if(currency.isEmpty()){
            errorLiveData.setValue("Scegli una valuta");
        } else if(budget.length() > 9){
            errorLiveData.setValue("Inserisci un numero più basso");
        } else {
            errorLiveData.setValue(null);
        }

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
    }

    public void insertExpense(String amount, String category, String description,
                                           String day, String month, String year, String inputCurrency) {
        String completedDate = day+"/"+month+"/"+year;
        String completedAmount;
        if (inputCurrency.equalsIgnoreCase(CURRENCY_EUR))
            completedAmount = amount + inputCurrency;
        else
            completedAmount = inputCurrency + amount;
        Expense expense = new Expense(completedAmount, category, description, completedDate, false);
        expenseRepository.insertExpense(expense);
        amountSpentLiveData.postValue(countAmount(getAllExpenses(), inputCurrency));
        fetchAllExpenses();
        expenseEvent.setValue(ADDED);
    }

    public void updateExpense(Expense expense, String currency, String amount,
                              String category, String description,
                              String day, String month, String year) {
        String realAmount = generateTextAmount(amount, currency);
        if(!expense.getAmount().equals(realAmount)){
            updateExpenseAmount(expense.getId(), realAmount, currency);
            expense.setAmount(realAmount);
        }
        if(!expense.getCategory().equals(category)){
            updateExpenseCategory(expense.getId(), category);
            expense.setCategory(category);
        }
        if(!expense.getDescription().equals(description)){
            updateExpenseDescription(expense.getId(), description);
            expense.setDescription(description);
        }
        String completeDate = day+"/"+month+"/"+year;
        if(!expense.getDate().equals(completeDate)){
            updateExpenseDate(expense.getId(), completeDate);
            expense.setDate(completeDate);
        }
        expenseEvent.setValue(UPDATED);
    }

    public void updateExpenseCategory(String expenseId, String newCategory) {
        expenseRepository.updateExpenseCategory(expenseId, newCategory);
        fetchAllExpenses();
    }

    public void updateExpenseDescription(String expenseId, String newDescription) {
        expenseRepository.updateExpenseDescription(expenseId, newDescription);
        fetchAllExpenses();
    }

    public void updateExpenseAmount(String expenseId, String newAmount, String currency) {
        amountSpentLiveData.postValue(countAmount(getAllExpenses(), currency));
        expenseRepository.updateExpenseAmount(expenseId, newAmount);
        fetchAllExpenses();
    }

    public void updateExpenseDate(String expenseId, String newDate) {
        expenseRepository.updateExpenseDate(expenseId, newDate);
        fetchAllExpenses();
    }

    public void updateExpenseIsSelected(String expenseId, boolean newIsSelected) {
        expenseRepository.updateExpenseIsSelected(expenseId, newIsSelected);
        fetchAllExpenses();
    }

    public void deleteSelectedExpenses(String currency) {
        List<Expense> selectedExpenses = getSelectedExpensesLiveData().getValue();
        if(selectedExpenses != null && !selectedExpenses.isEmpty()) {
            if (amountSpentLiveData.getValue() != null) {
                amountSpentLiveData.postValue(amountSpentLiveData.getValue() - countAmount(selectedExpenses, currency));
            }
            expenseRepository.deleteAllExpenses(selectedExpenses);
            selectedExpensesLiveData.postValue(Collections.emptyList());
            fetchAllExpenses();
            expenseEvent.setValue(DELETED);
        } else {
            expenseEvent.setValue(INVALID_DELETE);
        }
    }

    public int[] extractDayMonthYear(String date){
        int inputDay, inputMonth;
        int[] extractedDate = new int[3];
        if(date.charAt(1) == '/'){
            inputDay = (int)(date.charAt(0)) - '0';
            date = date.substring(2);
        }else{
            inputDay = Integer.parseInt(date.substring(0, 2));
            date = date.substring(3);
        }
        extractedDate[0] = inputDay;

        if(date.charAt(1) == '/'){
            inputMonth =(int)(date.charAt(0)) - '0';
            date = date.substring(2);
        }else{
            inputMonth = Integer.parseInt(date.substring(0, 2));
            date = date.substring(3);
        }
        extractedDate[1] = inputMonth;
        extractedDate[2] = Integer.parseInt(date);
        return extractedDate;
    }

    public String extractRealAmount(Expense expense, String currency){
        String tmp;
        String amount = expense.getAmount();
        if(currency.equalsIgnoreCase(CURRENCY_EUR))
            tmp = amount.substring(0, amount.length()-1);
        else
            tmp = amount.substring(1);
        return tmp;
    }

    public double countAmount(List<Expense> expenseList, String currency){
        double totExpense = 0;
        if(expenseList != null && !expenseList.isEmpty() && currency != null) {
            for (Expense e : expenseList) {
                String amount = e.getAmount();
                String realAmount;
                if (currency.equalsIgnoreCase(CURRENCY_EUR)) {
                    realAmount = amount.substring(0, amount.length() - 1);
                } else {
                    realAmount = amount.substring(1);
                }
                totExpense += Double.parseDouble(realAmount);
            }
        }
        return totExpense;
    }

    public String generateTextAmount(String totExpense, String currency){
        String tmp;
        if(currency.equalsIgnoreCase(CURRENCY_EUR))
            tmp = totExpense+currency;
        else
            tmp = currency+totExpense;
        return tmp;
    }

    public List<Expense> getAllExpenses() {
        fetchAllExpenses();
        return expensesLiveData.getValue();
    }

    public List<Expense> getFilteredExpenses(String category) {
        filteredExpensesLiveData.setValue(expenseRepository.getFilteredExpenses(category));
        return filteredExpensesLiveData.getValue();
    }

    public void toggleExpenseSelection(Expense expense) {
        boolean isSelected = expense.isExpense_isSelected();
        expense.setExpense_isSelected(!isSelected);
        updateExpenseIsSelected(expense.getId(), !isSelected);
        selectedExpensesLiveData.setValue(expenseRepository.getSelectedExpenses());
        fetchAllExpenses();
    }

    public void deselectAllExpenses() {
        fetchAllExpenses();
        List<Expense> expenses = expensesLiveData.getValue();
        if(expenses != null) {
            for (Expense expense : expenses) {
                expense.setExpense_isSelected(false);
                updateExpenseIsSelected(expense.getId(), false);
            }
            expensesLiveData.setValue(expenses);
            selectedExpensesLiveData.postValue(Collections.emptyList());
        }
    }

}
