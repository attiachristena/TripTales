package com.unimib.triptales.ui.diary.viewmodel;

import static com.unimib.triptales.util.Constants.CURRENCY_EUR;

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

    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Expense>> selectedExpensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Expense>> filteredExpensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> amountSpentLiveData = new MutableLiveData<>();

    public ExpenseViewModel(IExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public LiveData<List<Expense>> getExpensesLiveData() {
        return expensesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
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
        try {
            List<Expense> expenses = expenseRepository.getAllExpenses();
            expensesLiveData.postValue(expenses);
            loadingLiveData.postValue(false);
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue(e.getMessage());
        }
    }

    public boolean validateInputExpense(String amount, String category, String description,
                                        String day, String month, String year) {
        boolean correct = true;
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

        if(errorLiveData.getValue() != null) correct = false;
        return correct;
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

    public MutableLiveData<List<Expense>> insertExpense(String amount, String category, String description,
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
        return expensesLiveData;
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
    }

    public void updateAllExpenses(List<Expense> expenses, String currency) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateAllExpenses(expenses);
            amountSpentLiveData.postValue(countAmount(getAllExpenses(), currency));
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nell'aggiornamento della lista di spese: "+e.getMessage());
        }
    }

    public void updateExpenseCategory(int expenseId, String newCategory) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseCategory(expenseId, newCategory);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della categoria della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseDescription(int expenseId, String newDescription) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseDescription(expenseId, newDescription);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della descrizione della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseAmount(int expenseId, String newAmount, String currency) {
        loadingLiveData.setValue(true);
        try {
            amountSpentLiveData.postValue(countAmount(getAllExpenses(), currency));
            expenseRepository.updateExpenseAmount(expenseId, newAmount);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento dell'importo della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseDate(int expenseId, String newDate) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseDate(expenseId, newDate);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento della data della spesa: " + e.getMessage());
        }
    }

    public void updateExpenseIsSelected(int expenseId, boolean newIsSelected) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.updateExpenseIsSelected(expenseId, newIsSelected);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nell'aggiornamento di isSelected: " + e.getMessage());
        }
    }

    public void deleteExpense(Expense expense) {
        loadingLiveData.setValue(true);
        try {
            expenseRepository.deleteExpense(expense);
            fetchAllExpenses();
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            errorLiveData.postValue("Errore nella rimozione della spesa: "+e.getMessage());
        }
    }

    public void deleteSelectedExpenses(List<Expense> expenses, String currency) {
        if(amountSpentLiveData.getValue() != null){
            amountSpentLiveData.postValue(amountSpentLiveData.getValue()-countAmount(expenses, currency));
        }
        expenseRepository.deleteAllExpenses(expenses);
        selectedExpensesLiveData.postValue(Collections.emptyList());
        fetchAllExpenses();
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
        if(!expenseList.isEmpty()) {
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
        loadingLiveData.setValue(true);
        List<Expense> expenses = new ArrayList<>();
        try {
            expenses = expenseRepository.getAllExpenses();
        } catch (Exception e) {
            expenses = Collections.emptyList();
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione di tutte le spese: "+e.getMessage());
        }
        return expenses;
    }

    public List<Expense> getSelectedExpenses() {
        loadingLiveData.setValue(true);
        List<Expense> expenses;
        try {
            expenses = expenseRepository.getSelectedExpenses();
             selectedExpensesLiveData.postValue(expenseRepository.getSelectedExpenses());
        } catch (Exception e) {
            loadingLiveData.postValue(false);
            expenses = Collections.emptyList();
            selectedExpensesLiveData.postValue(Collections.emptyList());
            errorLiveData.postValue
                    ("Errore nella restituzione delle spese selezionate: "+e.getMessage());
        }
        return expenses;
    }


    public List<Expense> getFilteredExpenses(String category) {
        loadingLiveData.setValue(true);
        List<Expense> expenses = new ArrayList<>();
        try {
            expenses = expenseRepository.getFilteredExpenses(category);
        } catch (Exception e) {
            expenses = Collections.emptyList();
            loadingLiveData.postValue(false);
            errorLiveData.postValue
                    ("Errore nella restituzione delle spese filtrate: "+e.getMessage());
        }
        return expenses;
    }

    public void toggleExpenseSelection(Expense expense) {
        boolean isSelected = expense.isExpense_isSelected();
        expense.setExpense_isSelected(!isSelected);
        updateExpenseIsSelected(expense.getId(), !isSelected);
        List<Expense> selectedExpenses = getSelectedExpenses();
        selectedExpensesLiveData.postValue(selectedExpenses);
        fetchAllExpenses();
    }

    public void deselectAllExpenses() {
        List<Expense> expenses = getAllExpenses();
        for (Expense expense : expenses) {
            expense.setExpense_isSelected(false);
            updateExpenseIsSelected(expense.getId(), false);
        }
        expensesLiveData.setValue(expenses);
        selectedExpensesLiveData.postValue(Collections.emptyList());
        expenseRepository.updateAllExpenses(expenses);
    }

}
