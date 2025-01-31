package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.ADD_GOAL;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.EDIT_GOAL;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.GoalsRecyclerAdapter;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.viewmodel.GoalViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import java.util.List;

public class GoalsFragment extends Fragment {

    private CircularProgressIndicator progressIndicator;
    private View overlay_add_edit_goal;
    private FloatingActionButton addGoalButton;
    private EditText goalNameEditText;
    private EditText goalDescriptionEditText;
    private FloatingActionButton editGoalButton;
    private FloatingActionButton deleteGoalButton;
    private TextView progressTextView;
    private TextView noGoalsTextView;
    private GoalViewModel goalViewModel;
    private GoalsRecyclerAdapter goalsRecyclerAdapter;
    private boolean bAdd;
    private boolean bEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_obiettivi, container, false);
        IGoalRepository goalRepository = ServiceLocator.getINSTANCE().getGoalRepository(getContext());
        goalViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(goalRepository)).get(GoalViewModel.class);

        goalViewModel.deselectAllGoals();
        goalViewModel.getCheckedGoals();

        progressIndicator = view.findViewById(R.id.goalsProgressIndicator);
        progressTextView = view.findViewById(R.id.numObiettivi);
        updateProgressIndicator();

        RecyclerView recyclerViewGoals = view.findViewById(R.id.recyclerViewGoals);
        goalsRecyclerAdapter = new GoalsRecyclerAdapter(getContext());
        recyclerViewGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGoals.setAdapter(goalsRecyclerAdapter);

        goalsRecyclerAdapter.setOnGoalClickListener(new GoalsRecyclerAdapter.OnGoalClickListener() {
            @Override
            public void onGoalClick(Goal goal) {
                goalViewModel.toggleGoalSelection(goal);
            }
        });

        goalsRecyclerAdapter.setOnGoalCheckBoxClickListener(new GoalsRecyclerAdapter.OnGoalCheckBoxClickListener() {
            @Override
            public void onGoalCheckBoxClick(Goal goal) {
                goalViewModel.toggleGoalCheck(goal);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout rootLayoutGoals = view.findViewById(R.id.rootLayoutGoals);
        progressTextView = view.findViewById(R.id.numObiettivi);
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        addGoalButton = view.findViewById(R.id.addButtonGoals);
        editGoalButton = view.findViewById(R.id.modifyGoal);
        deleteGoalButton = view.findViewById(R.id.deleteGoal);
        progressIndicator = view.findViewById(R.id.goalsProgressIndicator);
        noGoalsTextView = view.findViewById(R.id.noGoalsString);
        bAdd = false;
        bEdit = false;

        goalViewModel.getGoalsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> goals) {
                goalsRecyclerAdapter.setGoalsList(goals);
                if(goals.isEmpty()){
                    noGoalsTextView.setVisibility(View.VISIBLE);
                } else {
                    noGoalsTextView.setVisibility(View.GONE);
                }
                updateProgressIndicator();
            }
        });

        overlay_add_edit_goal = inflater.inflate(R.layout.overlay_add_edit_goal, rootLayoutGoals, false);
        rootLayoutGoals.addView(overlay_add_edit_goal);
        overlay_add_edit_goal.setVisibility(View.GONE);

        goalViewModel.getSelectedGoalsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> selectedGoals) {
                if(selectedGoals != null){
                    if(selectedGoals.size() == 1){
                        if(overlay_add_edit_goal.getVisibility() == View.VISIBLE){
                            editGoalButton.setVisibility(View.GONE);
                            deleteGoalButton.setVisibility(View.GONE);
                        } else {
                            addGoalButton.setEnabled(false);
                            editGoalButton.setVisibility(View.VISIBLE);
                            deleteGoalButton.setVisibility(View.VISIBLE);
                        }
                    } else if(selectedGoals.size() == 2) {
                        addGoalButton.setEnabled(false);
                        editGoalButton.setVisibility(View.GONE);
                    } else if(selectedGoals.isEmpty()){
                        editGoalButton.setVisibility(View.GONE);
                        deleteGoalButton.setVisibility(View.GONE);
                        addGoalButton.setEnabled(true);
                    }
                }
            }
        });

        goalViewModel.getCheckedGoalsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> goals) {
                updateProgressIndicator();
            }
        });

        goalViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if(errorMessage != null){
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        goalViewModel.getGoalEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if(message != null){
                    switch (message) {
                        case ADDED:
                            Toast.makeText(requireActivity(), R.string.snackbarGoalAdded, Toast.LENGTH_SHORT).show();
                            break;
                        case UPDATED:
                            Toast.makeText(requireActivity(), R.string.snackbarGoalUpdated, Toast.LENGTH_SHORT).show();
                            break;
                        case DELETED:
                            Toast.makeText(requireActivity(), R.string.snackbarGoalDeleted, Toast.LENGTH_SHORT).show();
                            break;
                        case INVALID_DELETE:
                            Toast.makeText(requireActivity(), R.string.snackbarGoalNotDeleted, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        ImageButton goalBackButton = view.findViewById(R.id.backButtonGoal);
        Button saveGoalButton = view.findViewById(R.id.saveGoal);

        goalBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bEdit){
                    editGoalButton.setVisibility(View.VISIBLE);
                    deleteGoalButton.setVisibility(View.VISIBLE);
                }
                goalViewModel.setGoalOverlayVisibility(false);
            }
        });

        goalNameEditText = view.findViewById(R.id.inputGoalName);
        goalDescriptionEditText = view.findViewById(R.id.inputGoalDescription);

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bAdd = true;
                goalViewModel.setGoalOverlayVisibility(true);
            }
        });

        goalViewModel.getGoalOverlayVisibility().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                if(visible){
                    if(bAdd){
                        showOverlay(ADD_GOAL);
                    } else if(bEdit){
                        showOverlay(EDIT_GOAL);
                        editGoalButton.setVisibility(View.GONE);
                        deleteGoalButton.setVisibility(View.GONE);
                    }
                } else {
                    enableSwipeAndButtons(view);
                    if(bAdd){
                        hideOverlay(ADD_GOAL);
                    } else if(bEdit){
                        hideOverlay(EDIT_GOAL);
                    }
                }
            }
        });

        saveGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputGoalName = goalNameEditText.getText().toString().trim();
                String inputGoalDescription = goalDescriptionEditText.getText().toString().trim();

                boolean correct = goalViewModel.validateInputGoal(inputGoalName);

                if(correct){
                    if(bAdd){
                        goalViewModel.insertGoal(inputGoalName, inputGoalDescription);
                    } else if(bEdit){
                        List<Goal> selectedGoals = goalViewModel.getSelectedGoalsLiveData().getValue();
                        if(selectedGoals != null && !selectedGoals.isEmpty()){
                            Goal currentGoal = selectedGoals.get(0);
                            goalViewModel.updateGoal(currentGoal, inputGoalName, inputGoalDescription);
                            goalViewModel.deselectAllGoals();
                        }
                    }
                    goalViewModel.setGoalOverlayVisibility(false);
                }
            }
        });

        editGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bEdit = true;
                goalViewModel.setGoalOverlayVisibility(true);
            }
        });

        deleteGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goalViewModel.deleteSelectedGoals();
            }
        });

    }

    private void updateProgressIndicator(){
        List<Goal> goals = goalViewModel.getGoalsLiveData().getValue();
        List<Goal> checkedGoals = goalViewModel.getCheckedGoalsLiveData().getValue();
        int progressPercentage = 0;
        double numCheckedCards = 0;
        double numAllCards;
        if(goals != null) {
            numAllCards = goals.size();
            if(checkedGoals != null){
                numCheckedCards = checkedGoals.size();
            }
            if(!goals.isEmpty()){
                progressPercentage = (int) ((numCheckedCards / numAllCards) * 100);
            }
            progressIndicator.setProgress(progressPercentage);
            String tmp = getString(R.string.numObiettivi, (int) numCheckedCards, (int) numAllCards);
            progressTextView.setText(tmp);
        } else {
            progressIndicator.setProgress(progressPercentage);
            String tmp = getString(R.string.numObiettivi, 0, 0);
            progressTextView.setText(tmp);
        }
    }

    private void disableSwipeAndButtons(){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
        addGoalButton.setVisibility(View.GONE);
    }

    private void enableSwipeAndButtons(View view){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
        Constants.hideKeyboard(view, requireActivity());
        addGoalButton.setVisibility(View.VISIBLE);
    }

    private void showOverlay(String overlayType){
        disableSwipeAndButtons();
        overlay_add_edit_goal.setVisibility(View.VISIBLE);
        switch(overlayType){
            case ADD_GOAL:
                goalNameEditText.setText("");
                goalDescriptionEditText.setText("");
                break;
            case EDIT_GOAL:
                populateGoalField();
        }
    }

    private void hideOverlay(String overlayType){
        overlay_add_edit_goal.setVisibility(View.GONE);
        switch(overlayType){
            case ADD_GOAL:
                bAdd = false;
                break;
            case EDIT_GOAL:
                bEdit = false;
                break;
        }
    }

    private void populateGoalField(){
        List<Goal> selectedGoals = goalViewModel.getSelectedGoalsLiveData().getValue();
        if(selectedGoals != null && !selectedGoals.isEmpty()){
            Goal currentGoal = selectedGoals.get(0);
            goalNameEditText.setText(currentGoal.getName());
            goalDescriptionEditText.setText(currentGoal.getDescription());
        }
    }
}