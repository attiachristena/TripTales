package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.GoalsRecyclerAdapter;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.overlay.OverlayAddEditGoal;
import com.unimib.triptales.ui.diary.overlay.OverlayDelete;
import com.unimib.triptales.ui.diary.viewmodel.GoalViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import java.util.List;

public class GoalsFragment extends Fragment {

    private CircularProgressIndicator progressIndicator;
    private OverlayAddEditGoal overlayAddEditGoal;
    private FloatingActionButton addGoalButton;
    private FloatingActionButton editGoalButton;
    private FloatingActionButton deleteGoalButton;
    private TextView progressTextView;
    private TextView noGoalsTextView;
    private GoalViewModel goalViewModel;
    private GoalsRecyclerAdapter goalsRecyclerAdapter;
    private boolean bAdd;
    private boolean bEdit;
    private OverlayDelete overlayDelete;
    private FrameLayout darkBackground;

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
                new ViewModelFactory(goalRepository, requireActivity().getApplication())).get(GoalViewModel.class);

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

        ConstraintLayout diaryRootLayout = requireActivity().findViewById(R.id.rootLayoutDiary);
        progressTextView = view.findViewById(R.id.numObiettivi);
        addGoalButton = view.findViewById(R.id.addButtonGoals);
        editGoalButton = view.findViewById(R.id.modifyGoal);
        deleteGoalButton = view.findViewById(R.id.deleteGoal);
        progressIndicator = view.findViewById(R.id.goalsProgressIndicator);
        noGoalsTextView = view.findViewById(R.id.noGoalsString);
        darkBackground = requireActivity().findViewById(R.id.dark_background);
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

        // gestione aggiunta e modifica dell'obiettivo
        overlayAddEditGoal = new OverlayAddEditGoal(diaryRootLayout, requireContext());

        goalViewModel.getSelectedGoalsLiveData().observe(getViewLifecycleOwner(),
                selectedGoals -> {
            if(selectedGoals != null){
                if(selectedGoals.size() == 1){
                    if(Boolean.TRUE.equals(goalViewModel.getGoalOverlayVisibility().getValue())){
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

        addGoalButton.setOnClickListener(addGoalButtonListener -> {
            bAdd = true;
            goalViewModel.setGoalOverlayVisibility(true);
            overlayAddEditGoal.showOverlay(goalViewModel, bAdd, bEdit);
        });

        goalViewModel.getGoalOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                disableSwipeAndButtons();
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                enableSwipeAndButtons(view);
                darkBackground.setVisibility(View.GONE);
                if(bAdd){
                    bAdd = false;
                } else if(bEdit){
                    bEdit = false;
                }
            }
        });

        editGoalButton.setOnClickListener(editGoalButtonListener -> {
            bEdit = true;
            goalViewModel.setGoalOverlayVisibility(true);
            overlayAddEditGoal.showOverlay(goalViewModel, bAdd, bEdit);
        });


        // gestione eliminazione obiettivo
        overlayDelete = new OverlayDelete(diaryRootLayout, requireContext(), goalViewModel);

        goalViewModel.getDeleteOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                disableSwipeAndButtons();
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                enableSwipeAndButtons(view);
                darkBackground.setVisibility(View.GONE);
                List<Goal> selectedGoals = goalViewModel.getSelectedGoalsLiveData().getValue();
                if(selectedGoals != null && !selectedGoals.isEmpty()){
                    addGoalButton.setEnabled(false);
                }
            }
        });

        deleteGoalButton.setOnClickListener(deleteGoalButtonListener -> {
            goalViewModel.setDeleteOverlayVisibility(true);
            overlayDelete.showOverlay();
            TextView deleteText = requireActivity().findViewById(R.id.deleteText);
            TextView deleteDescriptionText = requireActivity()
                    .findViewById(R.id.deleteDescriptionText);
            deleteText.setText(R.string.delete_goal);
            deleteDescriptionText.setText(R.string.delete_goal_description);
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
}