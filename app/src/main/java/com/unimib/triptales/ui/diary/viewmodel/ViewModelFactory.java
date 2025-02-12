package com.unimib.triptales.ui.diary.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.repository.checkpointDiary.ICheckpointDiaryRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final IExpenseRepository expenseRepository;
    private final IGoalRepository goalRepository;
    private final ITaskRepository taskRepository;
    private final IDiaryRepository diaryRepository;
    private final ICheckpointDiaryRepository checkpointDiaryRepository;
    private final IImageCardItemRepository imageCardItemRepository;

    public ViewModelFactory(IExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
        this.goalRepository = null;
        this.taskRepository = null;
        diaryRepository = null;
        this.checkpointDiaryRepository = null;
        this.imageCardItemRepository = null;
    }

    public ViewModelFactory(IGoalRepository goalRepository){
        this.expenseRepository = null;
        this.goalRepository = goalRepository;
        this.taskRepository = null;
        diaryRepository = null;
        this.checkpointDiaryRepository = null;
        this.imageCardItemRepository = null;
    }

    public ViewModelFactory(ITaskRepository taskRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = taskRepository;
        diaryRepository = null;
        this.checkpointDiaryRepository = null;
        this.imageCardItemRepository = null;
    }

    public ViewModelFactory(ICheckpointDiaryRepository checkpointDiaryRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = null;
        this.checkpointDiaryRepository = checkpointDiaryRepository;
        this.imageCardItemRepository = null;
        diaryRepository = null;
    }

    public ViewModelFactory(IDiaryRepository diaryRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = null;
        this.diaryRepository = diaryRepository;
        this.checkpointDiaryRepository = null;
        this.imageCardItemRepository = null;
    }

    public ViewModelFactory(IImageCardItemRepository imageCardItemRepository){
        this.expenseRepository = null;
        this.goalRepository = null;
        this.taskRepository = null;
        this.checkpointDiaryRepository = null;
        this.imageCardItemRepository = imageCardItemRepository;
        diaryRepository = null;
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
        if (modelClass.isAssignableFrom(CheckpointDiaryViewModel.class)) {
            return (T) new CheckpointDiaryViewModel(checkpointDiaryRepository);
        }
        if (modelClass.isAssignableFrom(ImageCardItemViewModel.class)) {
            return (T) new ImageCardItemViewModel(imageCardItemRepository);
        }
        if(modelClass.isAssignableFrom(HomeViewModel.class)){
            return (T) new HomeViewModel(diaryRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }


}
