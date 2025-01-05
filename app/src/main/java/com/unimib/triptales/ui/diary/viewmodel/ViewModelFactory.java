package com.unimib.triptales.ui.diary.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.task.ITaskRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final IExpenseRepository expenseRepository;
    private final IGoalRepository goalRepository;
    private final ITaskRepository taskRepository;

    public ViewModelFactory(IExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
        this.goalRepository = null;
        this.taskRepository = null;
    }

    public ViewModelFactory(IGoalRepository goalRepository){
        this.expenseRepository = null;
        this.goalRepository = goalRepository;
        this.taskRepository = null;
    }

    public ViewModelFactory(ITaskRepository taskRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = taskRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExpenseViewModel.class)) {
            return (T) new ExpenseViewModel(expenseRepository);
        }
        if (modelClass.isAssignableFrom(GoalViewModel.class)) {
            return (T) new GoalViewModel(goalRepository);
        }
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(taskRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}
