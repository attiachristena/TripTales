package com.unimib.triptales.ui.diario.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.triptales.R;

import java.util.ArrayList;
import java.util.Iterator;


public class SpeseFragment extends Fragment {

    int budget;
    int spent;
    Button saveButton;
    TextView progressText;
    ImageButton editBudget;
    CardView cardAddBudget;
    ImageButton backButton;
    int progressPercentage;
    LinearProgressIndicator progressIndicator;
    TextView budgetText;
    EditText numberEditText;
    String formattedText;
    ImageButton addSpesa;
    LinearLayout spesaCardsContainer;
    ImageButton backButtonSpesa;
    CardView cardAddSpesa;
    Button saveSpesa;
    String inputQuantitaSpesa;
    String inputCategoria;
    String inputDescrizione;
    String inputData;
    EditText editTextQuantitaSpesa;
    EditText editTextCategoria;
    EditText editTextDescrizione;
    EditText editTextData;
    ConstraintLayout rootLayout;
    FloatingActionButton modificaSpesa;
    FloatingActionButton eliminaSpesa;
    ArrayList<MaterialCardView> listSpesaCards = new ArrayList<>();
    int indice;
    View overlay_add_spesa;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spese, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Card per modificare il budget

        saveButton = view.findViewById(R.id.salvaBudget);
        editBudget = view.findViewById(R.id.editBudget);
        cardAddBudget = view.findViewById(R.id.cardAddBudget);
        backButton = view.findViewById(R.id.backBudgetButton);

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAddBudget.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAddBudget.setVisibility(View.GONE);
                hideKeyboard(numberEditText);
            }
        });

        progressText = view.findViewById(R.id.progressText);
        numberEditText = view.findViewById(R.id.inputBudget);
        budgetText = view.findViewById(R.id.totBudget);
        progressIndicator = view.findViewById(R.id.budgetProgressIndicator);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = numberEditText.getText().toString().trim();
                if (inputText.isEmpty()) {
                    numberEditText.setError("Il campo non può essere vuoto");
                } else if (inputText.length() > 8) {
                    numberEditText.setError("Inserisci un numero più basso");
                } else {
                    // Testo valido
                    numberEditText.setError(null);
                    budget = Integer.parseInt(inputText);
                    budgetText.setText(getString(R.string.stringaCosto, budget));
                    updateProgressIndicator(spent, budget, 0);
                    hideKeyboard(numberEditText);
                    cardAddBudget.setVisibility(View.GONE);
                }
            }
        });

        //Card per aggiungere una nuova spesa

        addSpesa = view.findViewById(R.id.addButtonSpese);
        rootLayout = view.findViewById(R.id.rootLayout);
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        overlay_add_spesa = inflater.inflate(R.layout.overlay_add_spesa, rootLayout, false);
        rootLayout.addView(overlay_add_spesa);
        overlay_add_spesa.setVisibility(View.GONE);

        addSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(budget == 0){
                    Snackbar snackbar = Snackbar.make(rootLayout, R.string.snackbarErroreBudget, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else {
                    if(listSpesaCards.isEmpty()) {
                        rootLayout.removeView(overlay_add_spesa);
                        overlay_add_spesa.setVisibility(View.VISIBLE);
                    }
                    rootLayout.addView(overlay_add_spesa);
                    addSpesa.setVisibility(View.GONE);
                    editTextQuantitaSpesa.setText("");
                    editTextCategoria.setText("");
                    editTextDescrizione.setText("");
                    editTextData.setText("");
                }
            }
        });

        backButtonSpesa = view.findViewById(R.id.backSpesaButton);

        backButtonSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.removeView(overlay_add_spesa);
                hideKeyboard(editTextData);
                addSpesa.setVisibility(View.VISIBLE);
            }
        });

        spesaCardsContainer = view.findViewById(R.id.spesaCardsContainer);
        saveSpesa = view.findViewById(R.id.salvaSpesa);
        editTextQuantitaSpesa = view.findViewById(R.id.inputQuantitaSpesa);
        editTextCategoria = view.findViewById(R.id.inputCategory);
        editTextDescrizione = view.findViewById(R.id.inputDescription);
        editTextData = view.findViewById(R.id.inputDate);
        indice = 0;
        modificaSpesa = view.findViewById(R.id.modificaSpesa);
        eliminaSpesa = view.findViewById(R.id.eliminaSpesa);

        saveSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Usa LayoutInflater per creare una nuova CardView
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View newCard = inflater.inflate(R.layout.item_card_spesa, spesaCardsContainer, false);

                // Modifica il contenuto della card
                TextView category = newCard.findViewById(R.id.categoryTextView);
                inputCategoria = editTextCategoria.getText().toString().trim();

                TextView description = newCard.findViewById(R.id.descriptionTextView);
                inputDescrizione = editTextDescrizione.getText().toString().trim();

                TextView dataSpesa = newCard.findViewById(R.id.dateTextView);
                inputData = editTextData.getText().toString().trim();

                TextView amount = newCard.findViewById(R.id.amountTextView);
                inputQuantitaSpesa = editTextQuantitaSpesa.getText().toString().trim();


                if (inputQuantitaSpesa.isEmpty()) {
                    editTextQuantitaSpesa.setError("Inserisci una data");
                } else if (inputCategoria.isEmpty()) {
                    editTextCategoria.setError("Inserisci una categoria");
                } else if (inputCategoria.length() > 18){
                    editTextCategoria.setError("La categoria supera il massimo di caratteri");
                } else if (inputDescrizione.isEmpty()) {
                    editTextDescrizione.setError("Inserisci una descrizione");
                }else if (inputDescrizione.length() > 18){
                    editTextCategoria.setError("La descrizione supera il massimo di caratteri");
                } else if (inputData.isEmpty()) {
                    editTextData.setError("Inserisci una data");
                }else if (inputData.length() > 10){
                    editTextCategoria.setError("La data supera il massimo di caratteri");
                } else {
                    editTextCategoria.setError(null);
                    category.setText(inputCategoria);
                    editTextDescrizione.setError(null);
                    description.setText(inputDescrizione);
                    editTextData.setError(null);
                    dataSpesa.setText(inputData);
                    editTextQuantitaSpesa.setError(null);
                    int spesa = Integer.parseInt(inputQuantitaSpesa);
                    updateProgressIndicator(spesa, budget, 1);
                    amount.setText(getString(R.string.stringaCosto, spesa));
                    // Aggiungi la nuova CardView al layout genitore
                    spesaCardsContainer.addView(newCard);
                    hideKeyboard(editTextData);
                    rootLayout.removeView(overlay_add_spesa);
                    addSpesa.setVisibility(View.VISIBLE);
                    MaterialCardView cardSpesaCorrente = (MaterialCardView) spesaCardsContainer.getChildAt(indice);
                    listSpesaCards.add(cardSpesaCorrente);
                    indice++; //devi diminuire di uno quando elimini una card!!!

                    cardSpesaCorrente.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            addSpesa.setEnabled(false);
                            if (cardSpesaCorrente.isSelected()){
                                cardSpesaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary));
                                cardSpesaCorrente.setStrokeColor(getResources().getColor(R.color.primary));
                                cardSpesaCorrente.setSelected(false);
                                boolean notSelectedAll = true;
                                for (MaterialCardView m : listSpesaCards){
                                    if(m.isSelected())
                                        notSelectedAll = false;
                                }
                                if(notSelectedAll) {
                                    modificaSpesa.setVisibility(View.GONE);
                                    eliminaSpesa.setVisibility(View.GONE);
                                    addSpesa.setEnabled(true);
                                }
                            } else {
                                cardSpesaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
                                cardSpesaCorrente.setStrokeColor(getResources().getColor(R.color.background_dark));
                                cardSpesaCorrente.setSelected(true);
                            }
                            if (countSelectedCards(listSpesaCards) == 1) {
                                modificaSpesa.setVisibility(View.VISIBLE);
                                eliminaSpesa.setVisibility(View.VISIBLE);
                            } else if (countSelectedCards(listSpesaCards) == 2){
                                modificaSpesa.setVisibility(View.GONE);
                            }
                            return true;
                        }
                    });
                }
            }
        });

        eliminaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<MaterialCardView> iterator = listSpesaCards.iterator();
                while (iterator.hasNext()) {
                    MaterialCardView item = iterator.next();
                    if (item.isSelected()) {
                        TextView amountTextView = item.findViewById(R.id.amountTextView);
                        String amountText = amountTextView.getText().toString();
                        String amountSubstring = amountText.substring(0, amountText.length()-1);
                        int amount = Integer.parseInt(amountSubstring);
                        iterator.remove();
                        spesaCardsContainer.removeView(item);
                        spent = spent - amount;
                        updateProgressIndicator(spent, budget, 0);
                        indice--;
                    }
                }
                modificaSpesa.setVisibility(View.GONE);
                eliminaSpesa.setVisibility(View.GONE);
                addSpesa.setEnabled(true);
            }
        });

        modificaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.addView(overlay_add_spesa);
                addSpesa.setVisibility(View.GONE);

                MaterialCardView card = findSelectedCard(listSpesaCards);
                TextView cardSpesa = card.findViewById(R.id.amountTextView);
                editTextQuantitaSpesa.setText(cardSpesa.getText().toString().trim());
                TextView cardCategoria = card.findViewById(R.id.categoryTextView);
                editTextCategoria.setText(cardCategoria.getText().toString().trim());
                TextView cardDescrizione = card.findViewById(R.id.descriptionTextView);
                editTextDescrizione.setText(cardDescrizione.getText().toString().trim());
                TextView cardData = card.findViewById(R.id.dateTextView);
                editTextData.setText(cardData.getText().toString().trim());
            }
        });

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    // Metodo per nascondere la tastiera
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void updateProgressIndicator(int spesa, int budget, int add){
        if(add == 1)
            spent = spent+spesa;
        // calcola la percentuale spesa
        progressPercentage = (int) ((spent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.error));
        else
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.secondary));
        // imposta il progress indicator
        progressIndicator.setProgress(progressPercentage);
        // aggiorna la descrizione del progress indicator
        formattedText = getString(R.string.progress_text, spent, budget, progressPercentage);
        progressText.setText(formattedText);
    }

    public int countSelectedCards(ArrayList<MaterialCardView> cardList) {
        int selectedCount = 0;
        for (MaterialCardView card : cardList) {
            if (card.isSelected())
                selectedCount++;
        }
        return selectedCount;  // Restituisce il numero di card selezionate
    }

    public MaterialCardView findSelectedCard(ArrayList<MaterialCardView> cardList){
        MaterialCardView selectedCard = cardList.get(0);
        for (MaterialCardView card : cardList) {
            if (card.isSelected())
                selectedCard = card;
        }
        return selectedCard;
    }

}
