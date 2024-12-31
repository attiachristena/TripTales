package com.unimib.triptales.ui.diario.fragment;

import static com.unimib.triptales.util.Constants.findSelectedCard;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;

public class TasksFragment extends Fragment {

    ArrayList<MaterialCardView> allTasks;
    ArrayList<MaterialCardView> completedTasks;
    FloatingActionButton addTaskButton;
    View overlay_add_task;
    ConstraintLayout rootLayoutCheckList;
    LayoutInflater inflater;
    ImageButton backButtonTask;
    Button saveTask;
    EditText editTextTaskName;
    String inputTaskName;
    LinearLayout taskCardsContainer;
    int indice;
    FloatingActionButton modifyTask;
    FloatingActionButton deleteTask;
    CheckBox cardListCheckBox;
    View overlay_modify_task;
    EditText editTextModifiedTaskName;
    String inputModifiedTaskName;
    ImageButton backButtonModifyTask;
    Button saveModifiedTask;
    TextView noTasksString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allTasks = new ArrayList<MaterialCardView>();
        completedTasks = new ArrayList<MaterialCardView>();
        rootLayoutCheckList = view.findViewById(R.id.rootLayoutCheckList);
        inflater = LayoutInflater.from(view.getContext());

        overlay_add_task = inflater.inflate(R.layout.overlay_add_task, rootLayoutCheckList, false);
        rootLayoutCheckList.addView(overlay_add_task);
        overlay_add_task.setVisibility(View.GONE);

        addTaskButton = view.findViewById(R.id.addTaskButton);
        backButtonTask = view.findViewById(R.id.backButtonTask);
        saveTask = view.findViewById(R.id.saveTask);
        noTasksString = view.findViewById(R.id.noTasksString);

        backButtonTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_task.setVisibility(View.GONE);
                Constants.hideKeyboard(view, requireActivity());
                addTaskButton.setVisibility(View.VISIBLE);
            }
        });

        editTextTaskName = view.findViewById(R.id.inputTaskName);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_task.setVisibility(View.VISIBLE);
                addTaskButton.setVisibility(View.GONE);
                editTextTaskName.setText("");
            }
        });

        taskCardsContainer = view.findViewById(R.id.taskCardsContainer);
        indice = 0;
        modifyTask = view.findViewById(R.id.modifyTask);
        deleteTask = view.findViewById(R.id.deleteTask);

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Usa LayoutInflater per creare una nuova CardView
                View card = inflater.inflate(R.layout.item_card_list, taskCardsContainer, false);

                // Modifica il contenuto della card
                TextView name = card.findViewById(R.id.listItemNameTextView);
                inputTaskName = editTextTaskName.getText().toString().trim();

                if (inputTaskName.isEmpty()) {
                    editTextTaskName.setError("Inserisci un nome");
                } else {
                    editTextTaskName.setError(null);
                    name.setText(inputTaskName);

                    // Aggiungi la nuova CardView al layout genitore
                    taskCardsContainer.addView(card);
                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_task.setVisibility(View.GONE);
                    addTaskButton.setVisibility(View.VISIBLE);
                    MaterialCardView cardCurrentGoal = (MaterialCardView) taskCardsContainer.getChildAt(indice);
                    allTasks.add(cardCurrentGoal);
                    indice++;
                    noTasksString.setVisibility(View.GONE);

                    cardListCheckBox = cardCurrentGoal.findViewById(R.id.cardListCheckBox);

                    cardCurrentGoal.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            addTaskButton.setEnabled(false);
                            if (cardCurrentGoal.isSelected()){
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                cardCurrentGoal.setStrokeColor(getResources().getColor(R.color.light_gray));
                                cardCurrentGoal.setSelected(false);
                                cardListCheckBox.setEnabled(true);
                                boolean notSelectedAll = true;
                                for (MaterialCardView m : allTasks){
                                    if(m.isSelected())
                                        notSelectedAll = false;
                                }
                                if(notSelectedAll) {
                                    modifyTask.setVisibility(View.GONE);
                                    deleteTask.setVisibility(View.GONE);
                                    addTaskButton.setEnabled(true);
                                }
                            } else {
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.white));
                                cardCurrentGoal.setStrokeColor(getResources().getColor(R.color.background_dark));
                                cardCurrentGoal.setSelected(true);
                                cardListCheckBox.setEnabled(false);
                            }
                            /*if (countSelectedCards(allTasks) == 1) {
                                modifyTask.setVisibility(View.VISIBLE);
                                deleteTask.setVisibility(View.VISIBLE);
                            } else if (countSelectedCards(allTasks) == 2){
                                modifyTask.setVisibility(View.GONE);
                            }*/
                            return true;
                        }
                    });

                    cardCurrentGoal.setCheckable(true);

                    cardListCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!cardCurrentGoal.isChecked()){
                                completedTasks.add(cardCurrentGoal);
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
                                cardCurrentGoal.setChecked(true);
                                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else {
                                completedTasks.remove(cardCurrentGoal);
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                cardCurrentGoal.setChecked(false);
                                name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                        }
                    });
                }
            }
        });

        overlay_modify_task = inflater.inflate(R.layout.overlay_modify_task, rootLayoutCheckList, false);
        rootLayoutCheckList.addView(overlay_modify_task);
        overlay_modify_task.setVisibility(View.GONE);

        backButtonModifyTask = view.findViewById(R.id.backButtonModifyTask);
        saveModifiedTask = view.findViewById(R.id.saveModifiedTask);
        editTextModifiedTaskName = view.findViewById(R.id.inputModifiedTaskName);

        modifyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_task.setVisibility(View.VISIBLE);

                MaterialCardView card = findSelectedCard(allTasks);

                TextView cardName = card.findViewById(R.id.listItemNameTextView);
                editTextModifiedTaskName.setText(cardName.getText().toString().trim());

                addTaskButton.setVisibility(View.GONE);
                modifyTask.setVisibility(View.GONE);
                deleteTask.setVisibility(View.GONE);
            }
        });

        backButtonModifyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_task.setVisibility(View.GONE);
                Constants.hideKeyboard(view, requireActivity());
                addTaskButton.setVisibility(View.VISIBLE);
                modifyTask.setVisibility(View.VISIBLE);
                deleteTask.setVisibility(View.VISIBLE);
            }
        });

        saveModifiedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialCardView card = findSelectedCard(allTasks);

                // Modifica il contenuto della card
                TextView name = card.findViewById(R.id.listItemNameTextView);
                inputModifiedTaskName = editTextModifiedTaskName.getText().toString().trim();

                if (inputModifiedTaskName.isEmpty()) {
                    editTextModifiedTaskName.setError("Inserisci il nome dell'attività");
                } else {
                    editTextModifiedTaskName.setError(null);
                    name.setText(inputModifiedTaskName);
                    Constants.hideKeyboard(view, requireActivity());
                    overlay_modify_task.setVisibility(View.GONE);
                    addTaskButton.setVisibility(View.VISIBLE);
                    addTaskButton.setEnabled(true);
                    if(completedTasks.contains(card)) {
                        card.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
                        card.setStrokeColor(getResources().getColor(R.color.light_gray));
                    } else {
                        card.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                        card.setStrokeColor(getResources().getColor(R.color.light_gray));
                    }
                    card.setSelected(false);
                    cardListCheckBox.setEnabled(true);
                }
            }
        });

        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<MaterialCardView> iterator = allTasks.iterator();
                while (iterator.hasNext()) {
                    MaterialCardView item = iterator.next();
                    if (item.isSelected()) {
                        iterator.remove();
                        taskCardsContainer.removeView(item);
                        indice--;
                    }
                }
                modifyTask.setVisibility(View.GONE);
                deleteTask.setVisibility(View.GONE);
                addTaskButton.setEnabled(true);
                if(allTasks.isEmpty()){
                    noTasksString.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}