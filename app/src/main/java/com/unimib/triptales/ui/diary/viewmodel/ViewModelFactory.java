package com.unimib.triptales.ui.diary.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.triptales.repository.checkpoint.ICheckpointRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.task.ITaskRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final IExpenseRepository expenseRepository;
    private final IGoalRepository goalRepository;
    private final ITaskRepository taskRepository;
    private final ICheckpointRepository checkpointRepository;

    public ViewModelFactory(IExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
        this.goalRepository = null;
        this.taskRepository = null;
        this.checkpointRepository = null;
    }

    public ViewModelFactory(IGoalRepository goalRepository){
        this.expenseRepository = null;
        this.goalRepository = goalRepository;
        this.taskRepository = null;
        this.checkpointRepository = null;
    }

    public ViewModelFactory(ITaskRepository taskRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = taskRepository;
        this.checkpointRepository = null;
    }

    public ViewModelFactory(ICheckpointRepository checkpointRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = null;
        this.checkpointRepository = checkpointRepository;
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
        if (modelClass.isAssignableFrom(CheckpointViewModel.class)) {
            return (T) new CheckpointViewModel(checkpointRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}
