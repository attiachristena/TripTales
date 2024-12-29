package com.unimib.triptales.ui.diario.fragment;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.triptales.R;

import java.util.ArrayList;
import java.util.Iterator;

public class ObiettiviFragment extends Fragment {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_obiettivi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootLayoutGoals = view.findViewById(R.id.rootLayoutGoals);
        progressText = view.findViewById(R.id.numObiettivi);
        inflater = LayoutInflater.from(view.getContext());

        overlay_add_goal = inflater.inflate(R.layout.overlay_add_goal, rootLayoutGoals, false);
        rootLayoutGoals.addView(overlay_add_goal);
        overlay_add_goal.setVisibility(View.GONE);
        addButtonGoals = view.findViewById(R.id.addButtonGoals);
        backButtonGoal = view.findViewById(R.id.backButtonGoal);
        saveGoal = view.findViewById(R.id.saveGoal);
        goalsCards = new ArrayList<MaterialCardView>();
        checkedGoalsCards = new ArrayList<MaterialCardView>();
        progressText.setText(getString(R.string.numObiettivi, checkedGoalsCards.size(), goalsCards.size()));
        noGoalsString = view.findViewById(R.id.noGoalsString);

        backButtonGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_goal.setVisibility(View.GONE);
                hideKeyboard(view);
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

        goalCardsContainer = view.findViewById(R.id.goalCardsContainer);
        indice = 0;
        modifyGoal = view.findViewById(R.id.modifyGoal);
        deleteGoal = view.findViewById(R.id.deleteGoal);
        progressIndicator = view.findViewById(R.id.goalsProgressIndicator);

        saveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Usa LayoutInflater per creare una nuova CardView
                View card = inflater.inflate(R.layout.item_card_goal, goalCardsContainer, false);

                // Modifica il contenuto della card
                TextView name = card.findViewById(R.id.goalNameTextView);
                inputGoalName = editTextGoalName.getText().toString().trim();

                TextView description = card.findViewById(R.id.goalDescriptionTextView);
                inputGoalDescription = editTextGoalDescription.getText().toString().trim();

                if (inputGoalName.isEmpty()) {
                    editTextGoalName.setError("Inserisci un nome");
                } else {
                    editTextGoalName.setError(null);
                    name.setText(inputGoalName);
                    editTextGoalDescription.setError(null);
                    description.setText(inputGoalDescription);

                    // Aggiungi la nuova CardView al layout genitore
                    goalCardsContainer.addView(card);
                    hideKeyboard(view);
                    overlay_add_goal.setVisibility(View.GONE);
                    addButtonGoals.setVisibility(View.VISIBLE);
                    MaterialCardView cardCurrentGoal = (MaterialCardView) goalCardsContainer.getChildAt(indice);
                    goalsCards.add(cardCurrentGoal);
                    updateProgressIndicator();
                    indice++;
                    noGoalsString.setVisibility(View.GONE);

                    cardGoalCheckBox = cardCurrentGoal.findViewById(R.id.cardGoalCheckBox);

                    cardCurrentGoal.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            addButtonGoals.setEnabled(false);
                            if (cardCurrentGoal.isSelected()){
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                cardCurrentGoal.setStrokeColor(getResources().getColor(R.color.light_gray));
                                cardCurrentGoal.setSelected(false);
                                cardGoalCheckBox.setEnabled(true);
                                boolean notSelectedAll = true;
                                for (MaterialCardView m : goalsCards){
                                    if(m.isSelected())
                                        notSelectedAll = false;
                                }
                                if(notSelectedAll) {
                                    modifyGoal.setVisibility(View.GONE);
                                    deleteGoal.setVisibility(View.GONE);
                                    addButtonGoals.setEnabled(true);
                                }
                            } else {
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.white));
                                cardCurrentGoal.setStrokeColor(getResources().getColor(R.color.background_dark));
                                cardCurrentGoal.setSelected(true);
                                cardGoalCheckBox.setEnabled(false);
                            }
                            if (countSelectedCards() == 1) {
                                modifyGoal.setVisibility(View.VISIBLE);
                                deleteGoal.setVisibility(View.VISIBLE);
                            } else if (countSelectedCards() == 2){
                                modifyGoal.setVisibility(View.GONE);
                            }
                            return true;
                        }
                    });

                    cardCurrentGoal.setCheckable(true);

                    cardGoalCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!cardCurrentGoal.isChecked()){
                                checkedGoalsCards.add(cardCurrentGoal);
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
                                cardCurrentGoal.setChecked(true);
                            } else {
                                checkedGoalsCards.remove(cardCurrentGoal);
                                cardCurrentGoal.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                                cardCurrentGoal.setChecked(false);
                            }
                            updateProgressIndicator();
                        }
                    });
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

                MaterialCardView card = findSelectedCard();

                TextView cardName = card.findViewById(R.id.goalNameTextView);
                editTextModifiedGoalName.setText(cardName.getText().toString().trim());

                TextView cardDescrizione = card.findViewById(R.id.goalDescriptionTextView);
                editTextModifiedGoalDescription.setText(cardDescrizione.getText().toString().trim());

                addButtonGoals.setVisibility(View.GONE);
                modifyGoal.setVisibility(View.GONE);
                deleteGoal.setVisibility(View.GONE);
            }
        });

        backButtonModifiedGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_goal.setVisibility(View.GONE);
                hideKeyboard(view);
                addButtonGoals.setVisibility(View.VISIBLE);
                modifyGoal.setVisibility(View.VISIBLE);
                deleteGoal.setVisibility(View.VISIBLE);
            }
        });

        //modifica l'obiettivo
        saveModifiedGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialCardView card = findSelectedCard();

                // Modifica il contenuto della card
                TextView name = card.findViewById(R.id.goalNameTextView);
                inputModifiedGoalName = editTextModifiedGoalName.getText().toString().trim();

                TextView description = card.findViewById(R.id.goalDescriptionTextView);
                inputModifiedGoalDescription = editTextModifiedGoalDescription.getText().toString().trim();

                if (inputModifiedGoalName.isEmpty()) {
                    editTextModifiedGoalName.setError("Inserisci il nome dell'obiettivo");
                } else {
                    editTextModifiedGoalName.setError(null);
                    name.setText(inputModifiedGoalName);
                    editTextModifiedGoalDescription.setError(null);
                    description.setText(inputModifiedGoalDescription);
                    updateProgressIndicator();

                    hideKeyboard(view);
                    overlay_modify_goal.setVisibility(View.GONE);
                    addButtonGoals.setVisibility(View.VISIBLE);
                    addButtonGoals.setEnabled(true);
                    if(checkedGoalsCards.contains(card)) {
                        card.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
                        card.setStrokeColor(getResources().getColor(R.color.light_gray));
                    } else {
                        card.setCardBackgroundColor(getResources().getColor(R.color.light_gray));
                        card.setStrokeColor(getResources().getColor(R.color.light_gray));
                    }
                    card.setSelected(false);

                }
            }
        });

        //elimina l'obiettivo
        deleteGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<MaterialCardView> iterator = goalsCards.iterator();
                while (iterator.hasNext()) {
                    MaterialCardView item = iterator.next();
                    if (item.isSelected()) {
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
                }
            }
        });



    }

    public void updateProgressIndicator(){
        double numAllCards = goalsCards.size();
        double numCheckedCards = checkedGoalsCards.size();
        if(goalsCards.isEmpty()){
            progressPercentage = 0;
        } else {
            progressPercentage = (int) ((numCheckedCards / numAllCards) * 100);
            progressIndicator.setProgress(progressPercentage);
            String tmp = getString(R.string.numObiettivi, checkedGoalsCards.size(), goalsCards.size());
            progressText.setText(tmp);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public int countSelectedCards() {
        int selectedCount = 0;
        for (MaterialCardView card : goalsCards) {
            if (card.isSelected())
                selectedCount++;
        }
        return selectedCount;
    }

    //trova l'obiettivo selezionato nella lista di obiettivi inseriti
    public MaterialCardView findSelectedCard(){
        MaterialCardView selectedCard = goalsCards.get(0);
        for (MaterialCardView card : goalsCards) {
            if (card.isSelected())
                selectedCard = card;
        }
        return selectedCard;
    }


}