package com.unimib.triptales.ui.diario.fragment;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.GoalsRecyclerAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.GoalDao;
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment {

    ArrayList<MaterialCardView> goalsCards;
    ArrayList<MaterialCardView> checkedGoalsCards;
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
    LinearLayout goalCardsContainer;
    int indice;
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
    CheckBox cardGoalCheckBox;
    TextView noGoalsString;
    RecyclerView recyclerViewGoals;
    private List<Goal> goalsList = new ArrayList<>();
    private List<Goal> checkedGoals;
    private List<Goal> selectedGoals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();
        goalsList = goalDao.getAll();
        return inflater.inflate(R.layout.fragment_obiettivi, container, false);
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

        GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();

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
                addButtonGoals.setVisibility(View.GONE);
                editTextGoalName.setText("");
                editTextGoalDescription.setText("");
            }
        });

        saveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Goal currentGoal;
                GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();

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

                    goalsList.add(currentGoal);
                    recyclerAdapter.notifyItemInserted(goalsList.size()-1);
                    goalDao.insert(currentGoal);

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_goal.setVisibility(View.GONE);
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

                GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();
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
                Constants.hideKeyboard(view, requireActivity());
                addButtonGoals.setVisibility(View.VISIBLE);
                modifyGoal.setVisibility(View.VISIBLE);
                deleteGoal.setVisibility(View.VISIBLE);
            }
        });

        //modifica l'obiettivo
        saveModifiedGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MaterialCardView card = findSelectedCard(goalsCards);

                GoalDao goalDao = AppRoomDatabase.getDatabase(getContext()).goalDao();
                selectedGoals = goalDao.getSelectedGoals();
                Goal currentGoal = selectedGoals.get(0);

                // Modifica il contenuto della card
                //TextView name = card.findViewById(R.id.goalNameTextView);
                inputModifiedGoalName = editTextModifiedGoalName.getText().toString().trim();

                //TextView description = card.findViewById(R.id.goalDescriptionTextView);
                inputModifiedGoalDescription = editTextModifiedGoalDescription.getText().toString().trim();

                if (inputModifiedGoalName.isEmpty()) {
                    editTextModifiedGoalName.setError("Inserisci il nome dell'obiettivo");
                } else {
                    editTextModifiedGoalName.setError(null);
                    //name.setText(inputModifiedGoalName);
                    currentGoal.setName(inputModifiedGoalName);
                    goalDao.updateName(currentGoal.getId(), inputModifiedGoalName);
                    editTextModifiedGoalDescription.setError(null);
                    //description.setText(inputModifiedGoalDescription);
                    currentGoal.setDescription(inputModifiedGoalDescription);
                    goalDao.updateDescription(currentGoal.getId(), inputModifiedGoalDescription);

                    currentGoal.setGoal_isSelected(false);
                    goalDao.updateIsSelected(currentGoal.getId(), false);

                    int position = goalsList.indexOf(currentGoal);
                    if (position != -1) {
                        goalsList.set(position, currentGoal); // Aggiorna l'oggetto nella lista
                        recyclerAdapter.notifyItemChanged(position);
                    }

                    updateProgressIndicator();

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_modify_goal.setVisibility(View.GONE);
                    addButtonGoals.setVisibility(View.VISIBLE);
                    addButtonGoals.setEnabled(true);
                }
            }
        });

        //elimina l'obiettivo
        deleteGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Iterator<MaterialCardView> iterator = goalsCards.iterator();
                while (iterator.hasNext()) {
                    MaterialCardView item = iterator.next();
                    if (item.expense_isSelected()) {
                        iterator.remove();
                        goalCardsContainer.removeView(item);
                        updateProgressIndicator();
                        indice--;
                    }
                }
                modifyGoal.setVisibility(View.GONE);
                deleteGoal.setVisibility(View.GONE);
                addButtonGoals.setEnabled(true);
                if(goalsCards.isEmpty()){
                    noGoalsString.setVisibility(View.VISIBLE);
                }*/
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
            progressIndicator.setProgress(progressPercentage);
            String tmp = getString(R.string.numObiettivi, checkedGoals.size(), goalsList.size());
            progressText.setText(tmp);
        }
    }


}