package com.unimib.triptales.ui.diary.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;
import com.unimib.triptales.ui.diary.viewmodel.GoalViewModel;
import com.unimib.triptales.ui.diary.viewmodel.TaskViewModel;

public class OverlayDelete {
    private final ViewGroup rootLayout;
    private final View overlayView;
    private final ExpenseViewModel expenseViewModel;
    private final GoalViewModel goalViewModel;
    private final TaskViewModel taskViewModel;

    public OverlayDelete(ViewGroup rootLayout, Context context, ExpenseViewModel expenseViewModel) {
        this.rootLayout = rootLayout;
        this.expenseViewModel = expenseViewModel;
        this.goalViewModel = null;
        this.taskViewModel = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_delete, rootLayout, false);
    }

    public OverlayDelete(ViewGroup rootLayout, Context context, GoalViewModel goalViewModel) {
        this.rootLayout = rootLayout;
        this.expenseViewModel = null;
        this.goalViewModel = goalViewModel;
        this.taskViewModel = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_delete, rootLayout, false);
    }

    public OverlayDelete(ViewGroup rootLayout, Context context, TaskViewModel taskViewModel) {
        this.rootLayout = rootLayout;
        this.expenseViewModel = null;
        this.goalViewModel = null;
        this.taskViewModel = taskViewModel;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_delete, rootLayout, false);
    }

    public void showOverlay(){
        Button deleteButton = overlayView.findViewById(R.id.deleteButton);
        Button cancelButton = overlayView.findViewById(R.id.cancelDeleteButton);

        deleteButton.setOnClickListener(deleteButtonListener -> {
            if(expenseViewModel != null) {
                expenseViewModel.deleteSelectedExpenses();
                expenseViewModel.setDeleteOverlayVisibility(false);
            } else if(goalViewModel != null) {
                goalViewModel.deleteSelectedGoals();
                goalViewModel.setDeleteOverlayVisibility(false);
            } else if(taskViewModel != null) {
                taskViewModel.deleteSelectedTasks();
                taskViewModel.setDeleteOverlayVisibility(false);
            }
            hideOverlay();
        });

        cancelButton.setOnClickListener(cancelButtonListener -> {
            if(expenseViewModel != null) {
                expenseViewModel.setDeleteOverlayVisibility(false);
            } else if(goalViewModel != null) {
                goalViewModel.setDeleteOverlayVisibility(false);
            } else if(taskViewModel != null) {
                taskViewModel.setDeleteOverlayVisibility(false);
            }
            hideOverlay();
        });

        rootLayout.addView(overlayView);
    }

    public void hideOverlay(){
        rootLayout.removeView(overlayView);
    }
}
