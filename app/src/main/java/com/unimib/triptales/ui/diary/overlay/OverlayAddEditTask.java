package com.unimib.triptales.ui.diary.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.unimib.triptales.R;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.ui.diary.viewmodel.TaskViewModel;

import java.util.List;

public class OverlayAddEditTask {
    private final Context context;
    private final ViewGroup rootLayout;
    private final TaskViewModel taskViewModel;
    private final View overlayView;

    public OverlayAddEditTask(ViewGroup rootLayout, Context context, TaskViewModel taskViewModel) {
        this.rootLayout = rootLayout;
        this.context = context;
        this.taskViewModel = taskViewModel;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_add_edit_task, rootLayout, false);
    }

    public void showOverlay(boolean bAdd, boolean bEdit){
        ImageButton backButtonTask = overlayView.findViewById(R.id.backButtonTask);
        Button saveTask = overlayView.findViewById(R.id.saveTask);

        backButtonTask.setOnClickListener(backButtonTaskListener -> {
            taskViewModel.setTaskOverlayVisibility(false);
            hideOverlay();
        });

        EditText taskNameEditText = overlayView.findViewById(R.id.inputTaskName);

        if(bAdd){
            taskNameEditText.setText("");
        } else if(bEdit){
            populateGoalField(taskNameEditText);
        }

        saveTask.setOnClickListener(saveTaskListener -> {
            String inputTaskName = taskNameEditText.getText().toString().trim();

            boolean correct = taskViewModel.validateInputTask(inputTaskName);

            if(correct){
                if(bAdd){
                    taskViewModel.insertTask(inputTaskName, context);
                } else if(bEdit){
                    List<Task> selectedTasks = taskViewModel.getSelectedTasksLiveData().getValue();
                    if(selectedTasks != null && !selectedTasks.isEmpty()){
                        Task currentTask = selectedTasks.get(0);
                        taskViewModel.updateTask(currentTask, inputTaskName);
                        taskViewModel.deselectAllTasks();
                    }
                }
                taskViewModel.setTaskOverlayVisibility(false);
                hideOverlay();
            }
        });

        rootLayout.addView(overlayView);
    }

    public void hideOverlay(){
        rootLayout.removeView(overlayView);
    }

    private void populateGoalField(EditText taskNameEditText){
        List<Task> selectedTasks = taskViewModel.getSelectedTasksLiveData().getValue();
        if(selectedTasks != null && !selectedTasks.isEmpty()){
            Task currentTask = selectedTasks.get(0);
            taskNameEditText.setText(currentTask.getName());
        }
    }
}
