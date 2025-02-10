package com.unimib.triptales.ui.diary.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.unimib.triptales.R;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.ui.diary.viewmodel.GoalViewModel;

import java.util.List;

public class OverlayAddEditGoal {
    private final Context context;
    private final ViewGroup rootLayout;
    private final View overlayView;

    public OverlayAddEditGoal(ViewGroup rootLayout, Context context) {
        this.rootLayout = rootLayout;
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_add_edit_goal, rootLayout, false);
    }

    public void showOverlay(GoalViewModel goalViewModel, boolean bAdd, boolean bEdit){
        ImageButton goalBackButton = overlayView.findViewById(R.id.backButtonGoal);
        Button saveGoalButton = overlayView.findViewById(R.id.saveGoal);
        EditText goalNameEditText = overlayView.findViewById(R.id.inputGoalName);
        EditText goalDescriptionEditText = overlayView.findViewById(R.id.inputGoalDescription);

        if(bAdd){
            goalNameEditText.setText("");
            goalDescriptionEditText.setText("");
        } else if(bEdit){
            populateGoalField(goalViewModel, goalNameEditText, goalDescriptionEditText);
        }

        goalBackButton.setOnClickListener(goalBackButtonListener -> {
            goalViewModel.setGoalOverlayVisibility(false);
            hideOverlay();
        });

        saveGoalButton.setOnClickListener(saveGoalButtonListener -> {
            String inputGoalName = goalNameEditText.getText().toString().trim();
            String inputGoalDescription = goalDescriptionEditText.getText().toString().trim();

            boolean correct = goalViewModel.validateInputGoal(inputGoalName);

            if(correct){
                if(bAdd){
                    goalViewModel.insertGoal(inputGoalName, inputGoalDescription, context);
                } else if(bEdit){
                    List<Goal> selectedGoals = goalViewModel.getSelectedGoalsLiveData().getValue();
                    if(selectedGoals != null && !selectedGoals.isEmpty()){
                        Goal currentGoal = selectedGoals.get(0);
                        goalViewModel.updateGoal(currentGoal, inputGoalName, inputGoalDescription);
                        goalViewModel.deselectAllGoals();
                    }
                }
                goalViewModel.setGoalOverlayVisibility(false);
                hideOverlay();
            }
        });

        rootLayout.addView(overlayView);
    }

    public void hideOverlay(){
        rootLayout.removeView(overlayView);
    }

    private void populateGoalField(GoalViewModel goalViewModel, EditText goalNameEditText,
                                   EditText goalDescriptionEditText){
        List<Goal> selectedGoals = goalViewModel.getSelectedGoalsLiveData().getValue();
        if(selectedGoals != null && !selectedGoals.isEmpty()){
            Goal currentGoal = selectedGoals.get(0);
            goalNameEditText.setText(currentGoal.getName());
            goalDescriptionEditText.setText(currentGoal.getDescription());
        }
    }
}
