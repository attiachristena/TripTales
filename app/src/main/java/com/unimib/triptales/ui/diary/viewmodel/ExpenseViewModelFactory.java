package com.unimib.triptales.ui.diary.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.triptales.repository.expense.ExpenseRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.ui.login.viewmodel.UserViewModel;

public class ExpenseViewModelFactory implements ViewModelProvider.Factory {
    private final IExpenseRepository expenseRepository;

    public ExpenseViewModelFactory(IExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExpenseViewModel.class)) {
            return (T) new ExpenseViewModel(expenseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}
