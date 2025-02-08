package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.ADD_GOAL;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.DELETE_GOAL;
import static com.unimib.triptales.util.Constants.EDIT_GOAL;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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

import org.w3c.dom.Text;

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
    private View overlay_delete;

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

        RecyclerView recyclerViewGoals = view.findViewById(R.id.recyclerViewGoals);
        goalsRecyclerAdapter = new GoalsRecyclerAdapter(getContext());
        recyclerViewGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGoals.setAdapter(goalsRecyclerAdapter);

        goalsRecyclerAdapter.setOnGoalClickListener(goal ->
                goalViewModel.toggleGoalSelection(goal));

        goalsRecyclerAdapter.setOnGoalCheckBoxClickListener(goal ->
                goalViewModel.toggleGoalCheck(goal));

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

        progressTextView = view.findViewById(R.id.numObiettivi);
        updateProgressIndicator();

        goalViewModel.getGoalsLiveData().observe(getViewLifecycleOwner(), goals -> {
            goalsRecyclerAdapter.setGoalsList(goals);
            if(goals.isEmpty()){
                noGoalsTextView.setVisibility(View.VISIBLE);
            } else {
                noGoalsTextView.setVisibility(View.GONE);
            }
            updateProgressIndicator();
        });

        overlay_add_edit_goal = inflater.inflate(R.layout.overlay_add_edit_goal,
                rootLayoutGoals, false);
        rootLayoutGoals.addView(overlay_add_edit_goal);
        overlay_add_edit_goal.setVisibility(View.GONE);

        goalViewModel.getSelectedGoalsLiveData().observe(getViewLifecycleOwner(),
                selectedGoals -> {
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
        });

        goalViewModel.getCheckedGoalsLiveData().observe(getViewLifecycleOwner(),
                goals -> updateProgressIndicator());

        goalViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if(errorMessage != null){
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        goalViewModel.getGoalEvent().observe(getViewLifecycleOwner(), message -> {
            if(message != null){
                switch (message) {
                    case ADDED:
                        Toast.makeText(requireActivity(), R.string.snackbarGoalAdded,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATED:
                        Toast.makeText(requireActivity(), R.string.snackbarGoalUpdated,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DELETED:
                        Toast.makeText(requireActivity(), R.string.snackbarGoalDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_DELETE:
                        Toast.makeText(requireActivity(), R.string.snackbarGoalNotDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        ImageButton goalBackButton = view.findViewById(R.id.backButtonGoal);
        Button saveGoalButton = view.findViewById(R.id.saveGoal);

        goalBackButton.setOnClickListener(goalBackButtonListener ->
                goalViewModel.setGoalOverlayVisibility(false));

        goalNameEditText = view.findViewById(R.id.inputGoalName);
        goalDescriptionEditText = view.findViewById(R.id.inputGoalDescription);

        addGoalButton.setOnClickListener(addGoalButtonListener -> {
            bAdd = true;
            goalViewModel.setGoalOverlayVisibility(true);
        });

        goalViewModel.getGoalOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                if(bAdd){
                    showOverlay(ADD_GOAL);
                } else if(bEdit){
                    showOverlay(EDIT_GOAL);
                }
            } else {
                if(bAdd){
                    hideOverlay(ADD_GOAL, view);
                } else if(bEdit){
                    hideOverlay(EDIT_GOAL, view);
                }
            }
        });

        saveGoalButton.setOnClickListener(saveGoalButtonListener -> {
            String inputGoalName = goalNameEditText.getText().toString().trim();
            String inputGoalDescription = goalDescriptionEditText.getText().toString().trim();

            boolean correct = goalViewModel.validateInputGoal(inputGoalName);

            if(correct){
                if(bAdd){
                    goalViewModel.insertGoal(inputGoalName, inputGoalDescription, getContext());
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
        });

        editGoalButton.setOnClickListener(editGoalButtonListener -> {
            bEdit = true;
            goalViewModel.setGoalOverlayVisibility(true);
        });

        overlay_delete = inflater.inflate(R.layout.overlay_delete, rootLayoutGoals,
                false);
        rootLayoutGoals.addView(overlay_delete);
        overlay_delete.setVisibility(View.GONE);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button cancelDeleteButton = view.findViewById(R.id.cancelDeleteButton);
        TextView deleteText = view.findViewById(R.id.deleteText);
        TextView deleteDescriptionText = view.findViewById(R.id.deleteDescriptionText);
        deleteText.setText(R.string.delete_goal);
        deleteDescriptionText.setText(R.string.delete_goal_description);

        goalViewModel.getDeleteOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                showOverlay(DELETE_GOAL);
            } else {
                hideOverlay(DELETE_GOAL, view);
            }
        });

        cancelDeleteButton.setOnClickListener(cancelDeleteButtonListener -> {
                goalViewModel.setDeleteOverlayVisibility(false);
                addGoalButton.setEnabled(false);
        });

        deleteButton.setOnClickListener(deleteButtonListener -> {
            goalViewModel.deleteSelectedGoals();
            goalViewModel.setDeleteOverlayVisibility(false);
        });

        deleteGoalButton.setOnClickListener(deleteGoalButtonListener ->
                goalViewModel.setDeleteOverlayVisibility(true));

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
        addGoalButton.setEnabled(false);
        deleteGoalButton.setEnabled(false);
        editGoalButton.setEnabled(false);
    }

    private void enableSwipeAndButtons(View view){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
        Constants.hideKeyboard(view, requireActivity());
        addGoalButton.setEnabled(true);
        deleteGoalButton.setEnabled(true);
        editGoalButton.setEnabled(true);
    }

    private void showOverlay(String overlayType){
        disableSwipeAndButtons();
        switch(overlayType){
            case ADD_GOAL:
                overlay_add_edit_goal.setVisibility(View.VISIBLE);
                goalNameEditText.setText("");
                goalDescriptionEditText.setText("");
                break;
            case EDIT_GOAL:
                overlay_add_edit_goal.setVisibility(View.VISIBLE);
                populateGoalField();
                break;
            case DELETE_GOAL:
                overlay_delete.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideOverlay(String overlayType, View view){
        enableSwipeAndButtons(view);
        switch(overlayType){
            case ADD_GOAL:
                overlay_add_edit_goal.setVisibility(View.GONE);
                bAdd = false;
                break;
            case EDIT_GOAL:
                overlay_add_edit_goal.setVisibility(View.GONE);
                bEdit = false;
                break;
            case DELETE_GOAL:
                overlay_delete.setVisibility(View.GONE);
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