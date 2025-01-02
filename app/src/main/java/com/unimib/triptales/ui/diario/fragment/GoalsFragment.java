package com.unimib.triptales.ui.diario.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.GoalsRecyclerAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.GoalDao;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.ui.diario.DiaryActivity;
import com.unimib.triptales.util.Constants;
import java.util.Iterator;
import java.util.List;

public class GoalsFragment extends Fragment {

    int progressPercentage;
    CircularProgressIndicator progressIndicator;
    View overlay_add_goal;
    ConstraintLayout rootLayoutGoals;
    LayoutInflater inflater;
    FloatingActionButton addButtonGoals;
    ImageButton backButtonGoal;
    Button saveGoal;
    EditText editTextGoalName;
    EditText editTextGoalDescription;
    String inputGoalName;
    String inputGoalDescription;
    FloatingActionButton modifyGoal;
    FloatingActionButton deleteGoal;
    ImageButton backButtonModifiedGoal;
    Button saveModifiedGoal;
    EditText editTextModifiedGoalName;
    EditText editTextModifiedGoalDescription;
    String inputModifiedGoalName;
    String inputModifiedGoalDescription;
    View overlay_modify_goal;
    TextView progressText;
    TextView noGoalsString;
    RecyclerView recyclerViewGoals;
    private List<Goal> goalsList;
    private List<Goal> checkedGoals;
    private List<Goal> selectedGoals;
    GoalDao goalDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_obiettivi, container, false);
        goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();
        goalsList = goalDao.getAll();
        checkedGoals = goalDao.getCheckedGoals();
        progressIndicator = view.findViewById(R.id.goalsProgressIndicator);
        progressText = view.findViewById(R.id.numObiettivi);
        updateProgressIndicator();
        for(Goal g : goalsList){
            g.setGoal_isSelected(false);
            goalDao.updateIsSelected(g.getId(), false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootLayoutGoals = view.findViewById(R.id.rootLayoutGoals);
        progressText = view.findViewById(R.id.numObiettivi);
        inflater = LayoutInflater.from(view.getContext());

        addButtonGoals = view.findViewById(R.id.addButtonGoals);
        modifyGoal = view.findViewById(R.id.modifyGoal);
        deleteGoal = view.findViewById(R.id.deleteGoal);
        progressIndicator = view.findViewById(R.id.goalsProgressIndicator);

        goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();

        recyclerViewGoals = view.findViewById(R.id.recyclerViewGoals);
        GoalsRecyclerAdapter recyclerAdapter = new GoalsRecyclerAdapter(goalsList,  getContext(),
                addButtonGoals, modifyGoal, deleteGoal, progressIndicator, progressText);
        recyclerViewGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGoals.setAdapter(recyclerAdapter);

        overlay_add_goal = inflater.inflate(R.layout.overlay_add_goal, rootLayoutGoals, false);
        rootLayoutGoals.addView(overlay_add_goal);
        overlay_add_goal.setVisibility(View.GONE);
        backButtonGoal = view.findViewById(R.id.backButtonGoal);
        saveGoal = view.findViewById(R.id.saveGoal);
        checkedGoals = goalDao.getCheckedGoals();
        updateProgressIndicator();
        noGoalsString = view.findViewById(R.id.noGoalsString);
        if(goalsList.isEmpty()){
            noGoalsString.setVisibility(View.VISIBLE);
        } else {
            noGoalsString.setVisibility(View.GONE);
        }

        backButtonGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_goal.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addButtonGoals.setVisibility(View.VISIBLE);
            }
        });

        editTextGoalName = view.findViewById(R.id.inputGoalName);
        editTextGoalDescription = view.findViewById(R.id.inputGoalDescription);

        addButtonGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_goal.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
                addButtonGoals.setVisibility(View.GONE);
                editTextGoalName.setText("");
                editTextGoalDescription.setText("");
            }
        });

        saveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Goal currentGoal;

                inputGoalName = editTextGoalName.getText().toString().trim();
                inputGoalDescription = editTextGoalDescription.getText().toString().trim();

                if (inputGoalName.isEmpty()) {
                    editTextGoalName.setError("Inserisci un nome");
                } else {
                    editTextGoalName.setError(null);
                    editTextGoalDescription.setError(null);

                    if(inputGoalDescription.isEmpty()){
                        currentGoal = new Goal(inputGoalName, null,false, false);
                    } else {
                        currentGoal = new Goal(inputGoalName, inputGoalDescription, false, false);
                    }

                    long id = goalDao.insert(currentGoal);
                    currentGoal.setId((int) id);
                    goalsList.add(currentGoal);
                    recyclerAdapter.notifyItemInserted(goalsList.size() - 1);

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_goal.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addButtonGoals.setVisibility(View.VISIBLE);
                    updateProgressIndicator();
                    noGoalsString.setVisibility(View.GONE);
                }
            }
        });

        overlay_modify_goal = inflater.inflate(R.layout.overlay_modify_goal, rootLayoutGoals, false);
        rootLayoutGoals.addView(overlay_modify_goal);
        overlay_modify_goal.setVisibility(View.GONE);

        backButtonModifiedGoal = view.findViewById(R.id.backButtonModifiedGoal);
        saveModifiedGoal = view.findViewById(R.id.saveModifiedGoal);
        editTextModifiedGoalName = view.findViewById(R.id.inputModifiedGoalName);
        editTextModifiedGoalDescription = view.findViewById(R.id.inputModifiedGoalDescription);

        modifyGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_goal.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);

                selectedGoals = goalDao.getSelectedGoals();
                Goal currentGoal = selectedGoals.get(0);

                editTextModifiedGoalName.setText(currentGoal.getName());
                editTextModifiedGoalDescription.setText(currentGoal.getDescription());

                addButtonGoals.setVisibility(View.GONE);
                modifyGoal.setVisibility(View.GONE);
                deleteGoal.setVisibility(View.GONE);
            }
        });

        backButtonModifiedGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_goal.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addButtonGoals.setVisibility(View.VISIBLE);
                modifyGoal.setVisibility(View.VISIBLE);
                deleteGoal.setVisibility(View.VISIBLE);
            }
        });

        saveModifiedGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedGoals = goalDao.getSelectedGoals();
                Goal currentGoal = selectedGoals.get(0);

                inputModifiedGoalName = editTextModifiedGoalName.getText().toString().trim();
                inputModifiedGoalDescription = editTextModifiedGoalDescription.getText().toString().trim();

                if (inputModifiedGoalName.isEmpty()) {
                    editTextModifiedGoalName.setError("Inserisci il nome dell'obiettivo");
                } else {
                    editTextModifiedGoalName.setError(null);
                    currentGoal.setName(inputModifiedGoalName);
                    goalDao.updateName(currentGoal.getId(), inputModifiedGoalName);
                    editTextModifiedGoalDescription.setError(null);
                    currentGoal.setDescription(inputModifiedGoalDescription);
                    goalDao.updateDescription(currentGoal.getId(), inputModifiedGoalDescription);

                    currentGoal.setGoal_isSelected(false);
                    goalDao.updateIsSelected(currentGoal.getId(), false);

                    int position = goalsList.indexOf(currentGoal);
                    if (position != -1) {
                        goalsList.set(position, currentGoal);
                        recyclerAdapter.notifyItemChanged(position);
                    }

                    updateProgressIndicator();
                    Constants.hideKeyboard(view, requireActivity());
                    overlay_modify_goal.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addButtonGoals.setVisibility(View.VISIBLE);
                    addButtonGoals.setEnabled(true);
                }
            }
        });

        deleteGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;
                Iterator<Goal> iterator = goalsList.iterator();
                while (iterator.hasNext()) {
                    Goal g = iterator.next();
                    if (g.isGoal_isSelected()) {
                        iterator.remove();
                        recyclerAdapter.notifyItemRemoved(index);
                        goalDao.delete(g);
                    } else {
                        index++;
                    }
                }
                updateProgressIndicator();
                modifyGoal.setVisibility(View.GONE);
                deleteGoal.setVisibility(View.GONE);
                addButtonGoals.setEnabled(true);
                if(goalsList.isEmpty()){
                    noGoalsString.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void updateProgressIndicator(){
        double numAllCards = goalsList.size();
        double numCheckedCards = checkedGoals.size();
        if(goalsList.isEmpty()){
            progressPercentage = 0;
        } else {
            progressPercentage = (int) ((numCheckedCards / numAllCards) * 100);
        }
        progressIndicator.setProgress(progressPercentage);
        String tmp = getString(R.string.numObiettivi, checkedGoals.size(), goalsList.size());
        progressText.setText(tmp);
    }


}